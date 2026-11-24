package br.com.tcc.sistema.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.MvcResult;

/** CRUD e validações de gravação de cliente (CadastroCliente.fj). */
class ClienteApiTest extends ApiTestBase {

    @Test
    void criaEConsultaClientePessoaFisica() throws Exception {
        MvcResult criado = postar("/api/clientes", clienteValido());
        assertThat(criado.getResponse().getStatus()).isEqualTo(201);
        long id = idDoLocation(criado);

        MvcResult lido = consultar("/api/clientes/" + id);
        assertThat(lido.getResponse().getStatus()).isEqualTo(200);
        assertThat(corpo(lido))
                .contains("\"id\":" + id)
                .contains("\"nome\":\"João da Silva\"")
                .contains("\"cpfCnpj\":\"529.982.247-25\"")
                .contains("\"estado\":\"SC\"");
    }

    @Test
    void criaClientePessoaJuridica() throws Exception {
        Map<String, String> dados = clienteValido();
        dados.put("nome", "Padaria Exemplo & Cia Ltda.");
        dados.put("tipoPessoa", "J");
        dados.put("cpfCnpj", "11222333000181");
        assertThat(postar("/api/clientes", dados).getResponse().getStatus()).isEqualTo(201);
    }

    @Test
    void listaClientesEmOrdemDeId() throws Exception {
        long primeiro = criarCliente(clienteValido());
        Map<String, String> outro = clienteValido();
        outro.put("nome", "Maria de Souza");
        long segundo = criarCliente(outro);

        String lista = corpo(consultar("/api/clientes"));
        assertThat(lista.indexOf("\"id\":" + primeiro)).isGreaterThanOrEqualTo(0);
        assertThat(lista.indexOf("\"id\":" + primeiro)).isLessThan(lista.indexOf("\"id\":" + segundo));
    }

    @Test
    void atualizaCliente() throws Exception {
        long id = criarCliente(clienteValido());
        Map<String, String> novo = clienteValido();
        novo.put("nome", "João Pedro da Silva");
        novo.put("situacao", "I");

        MvcResult r = colocar("/api/clientes/" + id, novo);
        assertThat(r.getResponse().getStatus()).isEqualTo(200);
        assertThat(corpo(consultar("/api/clientes/" + id)))
                .contains("\"nome\":\"João Pedro da Silva\"")
                .contains("\"situacao\":\"I\"");
    }

    @Test
    void atualizacaoAplicaAsMesmasValidacoesDaInclusao() throws Exception {
        long id = criarCliente(clienteValido());
        Map<String, String> invalido = clienteValido();
        invalido.put("email", "sem-arroba");

        MvcResult r = colocar("/api/clientes/" + id, invalido);
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("E-mail inválido. Use o formato nome@dominio.com.");
    }

    @Test
    void excluiCliente() throws Exception {
        long id = criarCliente(clienteValido());
        assertThat(excluir("/api/clientes/" + id).getResponse().getStatus()).isEqualTo(204);

        MvcResult depois = consultar("/api/clientes/" + id);
        assertThat(depois.getResponse().getStatus()).isEqualTo(404);
        assertThat(corpo(depois)).contains("Cliente não encontrado.");
    }

    @Test
    void naoExcluiClienteComAtendimento() throws Exception {
        long clienteId = criarCliente(clienteValido());
        criarAtendimento(atendimentoValido(clienteId));

        MvcResult r = excluir("/api/clientes/" + clienteId);
        assertThat(r.getResponse().getStatus()).isEqualTo(409);
        assertThat(corpo(r)).contains("existem atendimentos vinculados");
        assertThat(consultar("/api/clientes/" + clienteId).getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void clienteInexistenteRetorna404() throws Exception {
        assertThat(consultar("/api/clientes/999999").getResponse().getStatus()).isEqualTo(404);
        assertThat(colocar("/api/clientes/999999", clienteValido()).getResponse().getStatus()).isEqualTo(404);
        assertThat(excluir("/api/clientes/999999").getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    void idNaoNumericoRetorna400() throws Exception {
        MvcResult r = consultar("/api/clientes/abc");
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("Identificador inválido");
    }

    @Test
    void corpoMalFormadoRetorna400() throws Exception {
        MvcResult r = postarTexto("/api/clientes", "{nome:");
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("Corpo da requisição inválido.");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "tipoPessoa||Informe o tipo de pessoa: F (física) ou J (jurídica).",
            "tipoPessoa|X|Tipo inválido. Use F (física) ou J (jurídica).",
            "nome||Informe o nome.",
            "nome|Maria|Pessoa física deve ter nome e sobrenome.",
            "cpfCnpj|123|CPF deve ter 11 dígitos.",
            "cpfCnpj|52998224726|CPF inválido. O dígito verificador não confere.",
            "situacao||Informe a situação: A (ativo) ou I (inativo).",
            "situacao|X|Situação inválida. Use A (ativo) ou I (inativo).",
            "estado|XX|UF inválida. Use a sigla do estado, por exemplo SP.",
            "cep|12345|CEP inválido. Use 00000000 ou 00000-000.",
            "cep|01001-000|O CEP não pertence à faixa da UF SC.",
            "telefone|123|Telefone inválido. Informe DDD + número, com 10 ou 11 dígitos.",
            "telefone|(11) 99999-8888|O DDD 11 não corresponde à UF SC.",
            "email||Informe o e-mail.",
            "email|sem-arroba|E-mail inválido. Use o formato nome@dominio.com."})
    void rejeitaCampoInvalidoComAMensagemDoLegado(String campo, String valor, String mensagem) throws Exception {
        Map<String, String> dados = clienteValido();
        dados.put(campo, valor);

        MvcResult r = postar("/api/clientes", dados);
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains(mensagem).contains("\"campo\":\"" + campo + "\"");
        // nada foi gravado
        assertThat(corpo(consultar("/api/clientes"))).isEqualTo("[]");
    }

    @Test
    void respeitaAOrdemDeValidacaoDoLegado() throws Exception {
        Map<String, String> dados = clienteValido();
        dados.put("tipoPessoa", "X");
        dados.put("nome", "");
        dados.put("email", "x");

        assertThat(corpo(postar("/api/clientes", dados))).contains("Tipo inválido. Use F (física) ou J (jurídica).");

        dados.put("tipoPessoa", "F");
        assertThat(corpo(postar("/api/clientes", dados))).contains("Informe o nome.");
    }

    @Test
    void tipoESituacaoAceitamMinusculasComoNoLegado() throws Exception {
        Map<String, String> dados = clienteValido();
        dados.put("tipoPessoa", "f");
        dados.put("situacao", "a");
        long id = criarCliente(dados);
        assertThat(corpo(consultar("/api/clientes/" + id))).contains("\"tipoPessoa\":\"f\"");
    }

    @Test
    void camposOpcionaisVaziosSaoGravadosComoNulo() throws Exception {
        Map<String, String> dados = clienteValido();
        dados.put("cidade", "");
        dados.put("bairro", null);
        long id = criarCliente(dados);
        assertThat(corpo(consultar("/api/clientes/" + id)))
                .contains("\"cidade\":null")
                .contains("\"bairro\":null");
    }

    @Test
    void limitesDeColunaEramImpostosPelaTelaELogoSaoValidadosNaApi() throws Exception {
        Map<String, String> dados = clienteValido();
        dados.put("cidade", "x".repeat(31));
        assertThat(corpo(postar("/api/clientes", dados))).contains("Cidade deve ter no máximo 30 caracteres.");

        dados = clienteValido();
        dados.put("numero", "1234567");
        assertThat(corpo(postar("/api/clientes", dados))).contains("Número deve ter no máximo 6 caracteres.");

        // CNPJ com pontuação completa (18 caracteres) não cabe em CPF_CNPJ VARCHAR(16).
        dados = clienteValido();
        dados.put("nome", "Padaria Exemplo Ltda");
        dados.put("tipoPessoa", "J");
        dados.put("cpfCnpj", "11.222.333/0001-81");
        assertThat(corpo(postar("/api/clientes", dados))).contains("CPF/CNPJ deve ter no máximo 16 caracteres.");
    }
}
