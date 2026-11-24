# Modernização do sistema legado (Unify NXJ → Spring Boot) — versão Claude

Migração do sistema de **cadastro de clientes e atendimentos** (Unify NXJ + Java) para
**Java 25 + Spring Boot 4.2.0 + H2 persistente em arquivo**. Somente backend (API REST); não há frontend.

## Como executar

Pré-requisitos: JDK 25 e Maven 3.9+.

```bash
cd claude
mvn test                  # testes automatizados
mvn spring-boot:run       # sobe a API em http://localhost:8080
# ou: mvn package && java -jar target/sistema-legado-1.0.0.jar
```

### Banco H2 (persistente em arquivo)

- URL: `jdbc:h2:file:./data/sistemalegado;AUTO_SERVER=TRUE` (usuário `sa`, senha vazia).
- O caminho é relativo ao diretório de onde a aplicação é iniciada. Rodando dentro de `claude/`,
  o arquivo fica em **`claude/data/sistemalegado.mv.db`**. Os dados permanecem após reiniciar.
- `schema.sql` roda a cada inicialização (`CREATE ... IF NOT EXISTS`), sem apagar dados.
- Console web: **http://localhost:8080/h2-console** (somente acesso local). Use a mesma URL JDBC acima,
  usuário `sa`, senha em branco. `AUTO_SERVER=TRUE` também permite abrir o arquivo em outro cliente SQL
  com a aplicação em execução.
- Para zerar os dados, pare a aplicação e apague a pasta `data/`.

## API

| Operação | Cliente (`CadastroCliente`) | Atendimento (`CadastroAtendimento`) |
|---|---|---|
| Listar | `GET /api/clientes` | `GET /api/atendimentos` |
| Consultar | `GET /api/clientes/{id}` | `GET /api/atendimentos/{id}` |
| Incluir | `POST /api/clientes` → 201 + `Location` | `POST /api/atendimentos` → 201 + `Location` |
| Alterar | `PUT /api/clientes/{id}` | `PUT /api/atendimentos/{id}` |
| Excluir | `DELETE /api/clientes/{id}` → 204 | `DELETE /api/atendimentos/{id}` → 204 |

Exemplo:

```bash
curl -X POST localhost:8080/api/clientes -H "Content-Type: application/json" -d '{
  "nome":"João da Silva","tipoPessoa":"F","cpfCnpj":"529.982.247-25","situacao":"A",
  "telefone":"(47) 99999-8888","email":"joao@exemplo.com","cep":"89280-000","estado":"SC",
  "cidade":"São Bento do Sul","bairro":"Centro","logradouro":"Rua Exemplo","numero":"100"}'

curl -X POST localhost:8080/api/atendimentos -H "Content-Type: application/json" -d '{
  "clienteId":"1","dataAbertura":"24/11/2026","assunto":"Primeiro contato",
  "descricao":"Cliente solicitou informações.","situacao":"A"}'
```

Erros: `{"mensagem":"<texto do legado>","campo":"<campo>"}` — `mensagem` é o texto que o legado exibia em
`displayToMessageBox` e `campo` o campo que ele reposicionava com `queueNextField`.
Status: **400** validação, **404** registro inexistente, **409** exclusão bloqueada por atendimentos vinculados.
Como no legado, apenas o **primeiro** erro, na mesma ordem do `validarGravacao()`, é retornado.

## Mapeamento legado → novo

| Legado | Novo | Observação |
|---|---|---|
| `scripts/cliente.sql` | `schema.sql` (CLIENTE, SEQ_CLIENTE) · `domain/entity/Cliente` | Colunas, tipos, tamanhos, NOT NULL, PK preservados |
| `scripts/atendimento.sql` | `schema.sql` (ATENDIMENTO, SEQ_ATENDIMENTO) · `domain/entity/Atendimento` | FK `FK_ATENDIMENTO_CLIENTE` preservada |
| `forms/CadastroCliente.fj` (`BEFORE ADD/UPDATE` → `validarGravacao`) | `service/ClienteService.validarGravacao` | Mesma sequência e mesmas mensagens |
| `forms/CadastroAtendimento.fj` (`validarGravacao`, `clienteInvalido`, `EXEC SQL select situacao`) | `service/AtendimentoService` · `ClienteRepository.buscarSituacaoPorId` | `session.status` 0/-35 mapeado em `ValidadorClienteAtendimento` |
| Operações padrão do form NXJ (add/query/update/delete) | `controller/ClienteController`, `controller/AtendimentoController` | REST |
| Campos dos `.fx/.jsp` | `dto/*RequestDTO`, `dto/*ResponseDTO` | Campos em `String`, como os campos de texto NXJ |
| `cliente/Texto` | `domain/validation/TextoUtil` | |
| `cliente/ValidadorCpf, Cnpj, Documento, Email, Nome, Telefone, Cep, Uf` | `domain/validation/Validador*` (mesmos nomes) | Portados sem alterar regras |
| `atendimento/ValidadorDataAbertura, ValidadorClienteAtendimento` | `domain/validation/Validador*` | |
| `NullableString`, `session.*`, `EXEC SQL`, `rejectOperation` | `String`, `ValidacaoNegocioException`, Spring Data JPA, `GlobalExceptionHandler` | Dependências Unify NXJ removidas |
| Mensagem de erro de FK do banco ao excluir cliente com atendimento | `ConflitoNegocioException` (409) | |
| Validação por campo (`ON DATA ACCEPT`), `queueNextField`, máscaras, `accell_type` | Não migrado (comportamento de tela) | `revisar()` de Nome/Documento foi mantido nas classes, sem uso pela API |

Fidelidade verificada: os 10 validadores foram comparados com o legado em **480.000 entradas aleatórias**
(compilando ambos e comparando mensagem de retorno), com **0 diferenças**.

## Diferenças em relação ao legado (e justificativas)

1. **`DEFAULT ON NULL NEXT VALUE FOR`** (sintaxe Oracle) → `DEFAULT NEXT VALUE FOR` no H2. O `ID` continua vindo
   da sequence (`NO CACHE`, `NO CYCLE` mantidos).
2. **Limites de tamanho das colunas** (`CPF_CNPJ` 16, `TELEFONE` 16, `CEP` 11, `CIDADE`/`BAIRRO` 30,
   `LOGRADOURO` 58, `NUMERO` 6): no legado o `field_length` da tela impedia a digitação excedente; na API
   precisam ser verificados (mensagem `"<Campo> deve ter no máximo N caracteres."`), executados **depois** de todas
   as validações originais. Consequência: um CNPJ com pontuação completa (18 caracteres) é rejeitado; enviar sem
   pontuação ou com pontuação parcial (≤ 16).
3. **Exclusão de cliente com atendimentos**: o banco legado recusa (FK sem `ON DELETE CASCADE`). Mantido, com a
   mensagem própria "Não é possível excluir o cliente: existem atendimentos vinculados a ele." (o legado não define texto).
4. **Mensagens novas** inevitáveis numa API: "Cliente não encontrado." / "Atendimento não encontrado." (404),
   "Identificador inválido. Informe apenas números." e "Corpo da requisição inválido." (400).
5. **Sem cascade/coleção** em `Cliente`: foi removido o `cascade = ALL` que existia no esqueleto anterior, pois
   excluiria atendimentos em silêncio, contradizendo a FK do legado.

## Hipóteses adotadas (ver dúvidas no chat)

- `"F" != tipo` / `"A" != valor` nos `.fj` foram lidos como **comparação por valor** (em Java puro seria por referência
  e nenhum cadastro funcionaria).
- `ID` é gerado pela sequence; `PUT/DELETE/GET` recebem o `id` na URL.
- `cidade`, `bairro`, `logradouro` e `numero` são **opcionais** (colunas nulas no `cliente.sql`; nenhum validador
  no `.fj`), embora o `.fx` os marque como `required`. Vazio é gravado como `NULL`.
- Valores são gravados como recebidos, apenas aparados (`trim`); `tipoPessoa`, `situacao` e `estado` em minúsculas
  são **aceitos** (a validação ignora caixa, como no legado) e **não** são convertidos.
- `dataAbertura` trafega em `dd/MM/yyyy` (formato do `.fx` e do `ValidadorDataAbertura`).
- Consulta: `GET` por id e listagem completa (todos os campos têm `findable="false"` no `.fx`).
- Não há autenticação, porque o legado fornecido não define nenhuma.
- CPF/CNPJ não é único (o schema não tem restrição de unicidade).
- CEP: tela com 9 posições e coluna com 11; o limite aplicado é o da coluna (11).

## Testes

`mvn test` executa:

- `domain/validation/*` — CPF, CNPJ, documento, e-mail, nome, telefone, CEP, UF, data de abertura, cliente do atendimento.
- `persistence/PersistenciaH2ArquivoTest` — schema.sql em H2 **arquivo**: dados e sequence sobrevivem a fechar/reabrir; FK e NOT NULL.
- `repository/RepositoryPersistenciaTest` — mapeamento JPA e sequences no H2.
- `api/ClienteApiTest`, `api/AtendimentoApiTest` — CRUD completo, ordem e mensagens de validação, regra de cliente ativo, bloqueio de exclusão.

Os testes usam H2 em memória (`src/test/resources/application.properties`), sem tocar em `./data`.
