package br.com.tcc.sistema.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base dos testes de API: sobe o contexto completo (controllers, services, JPA e H2 em memória)
 * e faz cada teste rodar em transação com rollback.
 */
@SpringBootTest
@Transactional
abstract class ApiTestBase {

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void configurarMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /** Monta um JSON simples (valores String ou null) a partir de um mapa. */
    static String json(Map<String, String> campos) {
        StringBuilder sb = new StringBuilder("{");
        boolean primeiro = true;
        for (Map.Entry<String, String> e : campos.entrySet()) {
            if (!primeiro) {
                sb.append(',');
            }
            primeiro = false;
            sb.append('"').append(e.getKey()).append("\":");
            sb.append(e.getValue() == null ? "null" : "\"" + e.getValue().replace("\"", "\\\"") + "\"");
        }
        return sb.append('}').toString();
    }

    static Map<String, String> clienteValido() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("nome", "João da Silva");
        m.put("tipoPessoa", "F");
        m.put("cpfCnpj", "529.982.247-25");
        m.put("situacao", "A");
        m.put("telefone", "(47) 99999-8888");
        m.put("email", "joao@exemplo.com");
        m.put("cep", "89280-000");
        m.put("estado", "SC");
        m.put("cidade", "São Bento do Sul");
        m.put("bairro", "Centro");
        m.put("logradouro", "Rua Exemplo");
        m.put("numero", "100");
        return m;
    }

    static Map<String, String> atendimentoValido(long clienteId) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("clienteId", String.valueOf(clienteId));
        m.put("dataAbertura", "24/11/2026");
        m.put("assunto", "Primeiro contato");
        m.put("descricao", "Cliente solicitou informações.");
        m.put("situacao", "A");
        return m;
    }

    MvcResult postar(String url, Map<String, String> corpo) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json(corpo))).andReturn();
    }

    MvcResult postarTexto(String url, String corpoBruto) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corpoBruto)).andReturn();
    }

    MvcResult colocar(String url, Map<String, String> corpo) throws Exception {
        return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json(corpo))).andReturn();
    }

    MvcResult consultar(String url) throws Exception {
        return mvc.perform(get(url)).andReturn();
    }

    MvcResult excluir(String url) throws Exception {
        return mvc.perform(delete(url)).andReturn();
    }

    static String corpo(MvcResult r) throws Exception {
        return r.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    /** Cria um cliente válido e devolve seu id (lido do cabeçalho Location). */
    long criarCliente(Map<String, String> dados) throws Exception {
        MvcResult r = postar("/api/clientes", dados);
        if (r.getResponse().getStatus() != 201) {
            throw new IllegalStateException("Falha ao criar cliente: " + corpo(r));
        }
        return idDoLocation(r);
    }

    long criarAtendimento(Map<String, String> dados) throws Exception {
        MvcResult r = postar("/api/atendimentos", dados);
        if (r.getResponse().getStatus() != 201) {
            throw new IllegalStateException("Falha ao criar atendimento: " + corpo(r));
        }
        return idDoLocation(r);
    }

    static long idDoLocation(MvcResult r) {
        String location = r.getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
