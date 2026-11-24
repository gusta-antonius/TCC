package br.com.tcc.sistema.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.MvcResult;

/** CRUD e validações de gravação de atendimento (CadastroAtendimento.fj). */
class AtendimentoApiTest extends ApiTestBase {

    @Test
    void criaEConsultaAtendimento() throws Exception {
        long clienteId = criarCliente(clienteValido());
        MvcResult criado = postar("/api/atendimentos", atendimentoValido(clienteId));
        assertThat(criado.getResponse().getStatus()).isEqualTo(201);
        long id = idDoLocation(criado);

        MvcResult lido = consultar("/api/atendimentos/" + id);
        assertThat(lido.getResponse().getStatus()).isEqualTo(200);
        assertThat(corpo(lido))
                .contains("\"id\":" + id)
                .contains("\"clienteId\":" + clienteId)
                .contains("\"dataAbertura\":\"24/11/2026\"")
                .contains("\"assunto\":\"Primeiro contato\"")
                .contains("\"situacao\":\"A\"");
    }

    @Test
    void descricaoEOpcional() throws Exception {
        long clienteId = criarCliente(clienteValido());
        Map<String, String> dados = atendimentoValido(clienteId);
        dados.put("descricao", "");
        long id = criarAtendimento(dados);
        assertThat(corpo(consultar("/api/atendimentos/" + id))).contains("\"descricao\":null");
    }

    @Test
    void atualizaAtendimento() throws Exception {
        long clienteId = criarCliente(clienteValido());
        long id = criarAtendimento(atendimentoValido(clienteId));

        Map<String, String> novo = atendimentoValido(clienteId);
        novo.put("situacao", "E");
        novo.put("assunto", "Contato encerrado");
        assertThat(colocar("/api/atendimentos/" + id, novo).getResponse().getStatus()).isEqualTo(200);
        assertThat(corpo(consultar("/api/atendimentos/" + id)))
                .contains("\"situacao\":\"E\"")
                .contains("\"assunto\":\"Contato encerrado\"");
    }

    @Test
    void excluiAtendimento() throws Exception {
        long clienteId = criarCliente(clienteValido());
        long id = criarAtendimento(atendimentoValido(clienteId));

        assertThat(excluir("/api/atendimentos/" + id).getResponse().getStatus()).isEqualTo(204);
        MvcResult depois = consultar("/api/atendimentos/" + id);
        assertThat(depois.getResponse().getStatus()).isEqualTo(404);
        assertThat(corpo(depois)).contains("Atendimento não encontrado.");
    }

    @Test
    void atendimentoInexistenteRetorna404() throws Exception {
        assertThat(consultar("/api/atendimentos/999999").getResponse().getStatus()).isEqualTo(404);
        assertThat(excluir("/api/atendimentos/999999").getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    void clienteInexistenteImpedeOAtendimento() throws Exception {
        MvcResult r = postar("/api/atendimentos", atendimentoValido(999999));
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("Cliente não encontrado.").contains("\"campo\":\"clienteId\"");
    }

    @Test
    void clienteInativoImpedeOAtendimento() throws Exception {
        Map<String, String> inativo = clienteValido();
        inativo.put("situacao", "I");
        long clienteId = criarCliente(inativo);

        MvcResult r = postar("/api/atendimentos", atendimentoValido(clienteId));
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("Cliente inativo.");
    }

    @Test
    void atualizacaoTambemConfereASituacaoDoCliente() throws Exception {
        long clienteId = criarCliente(clienteValido());
        long id = criarAtendimento(atendimentoValido(clienteId));

        Map<String, String> inativar = clienteValido();
        inativar.put("situacao", "I");
        assertThat(colocar("/api/clientes/" + clienteId, inativar).getResponse().getStatus()).isEqualTo(200);

        MvcResult r = colocar("/api/atendimentos/" + id, atendimentoValido(clienteId));
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("Cliente inativo.");
    }

    @Test
    void ordemDeValidacaoComecaPeloCliente() throws Exception {
        Map<String, String> dados = atendimentoValido(999999);
        dados.put("dataAbertura", "31/02/2026");
        assertThat(corpo(postar("/api/atendimentos", dados))).contains("Cliente não encontrado.");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "clienteId||Informe o cliente.",
            "clienteId|12a|Cliente deve conter apenas números.",
            "clienteId|12345678901|Cliente deve ter no máximo 10 dígitos.",
            "dataAbertura||Informe a data de abertura.",
            "dataAbertura|2026-11-24|Data de abertura inválida. Use dd/mm/aaaa.",
            "dataAbertura|29/02/2025|Data de abertura inválida. Use dd/mm/aaaa.",
            "situacao||Informe a situação: A (aberto) ou E (encerrado).",
            "situacao|I|Situação inválida. Use A (aberto) ou E (encerrado).",
            "assunto||Informe o assunto."})
    void rejeitaCampoInvalidoComAMensagemDoLegado(String campo, String valor, String mensagem) throws Exception {
        long clienteId = criarCliente(clienteValido());
        Map<String, String> dados = atendimentoValido(clienteId);
        dados.put(campo, valor);

        MvcResult r = postar("/api/atendimentos", dados);
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains(mensagem).contains("\"campo\":\"" + campo + "\"");
    }

    @Test
    void assuntoEDescricaoRespeitamOsLimites() throws Exception {
        long clienteId = criarCliente(clienteValido());

        Map<String, String> dados = atendimentoValido(clienteId);
        dados.put("assunto", "a".repeat(60));
        dados.put("descricao", "d".repeat(200));
        assertThat(postar("/api/atendimentos", dados).getResponse().getStatus()).isEqualTo(201);

        dados.put("assunto", "a".repeat(61));
        assertThat(corpo(postar("/api/atendimentos", dados))).contains("Assunto deve ter no máximo 60 caracteres.");

        dados.put("assunto", "ok");
        dados.put("descricao", "d".repeat(201));
        assertThat(corpo(postar("/api/atendimentos", dados))).contains("Descrição deve ter no máximo 200 caracteres.");
    }
}
