package br.com.tcc.sistema.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifica, com JDBC puro, o schema.sql e a persistência em arquivo do H2:
 * os dados (e a sequence) sobrevivem ao fechamento e à reabertura do banco.
 */
class PersistenciaH2ArquivoTest {

    private static final String INSERT_CLIENTE = """
            INSERT INTO CLIENTE (NOME, TIPO_PESSOA, CPF_CNPJ, SITUACAO, TELEFONE, EMAIL, CEP, ESTADO)
            VALUES ('Joao da Silva', 'F', '52998224725', 'A', '47999998888', 'joao@exemplo.com', '89280000', 'SC')
            """;

    @TempDir
    Path pasta;

    private Connection abrir() throws SQLException {
        String arquivo = pasta.resolve("banco").toAbsolutePath().toString().replace('\\', '/');
        return DriverManager.getConnection("jdbc:h2:file:" + arquivo + ";DB_CLOSE_ON_EXIT=FALSE", "sa", "");
    }

    /** Executa schema.sql como a aplicação faz em cada inicialização (o script é idempotente). */
    private void aplicarSchema(Connection c) throws SQLException, IOException {
        String script;
        try (InputStream in = getClass().getResourceAsStream("/schema.sql")) {
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        StringBuilder semComentarios = new StringBuilder();
        for (String linha : script.split("\\R")) {
            if (!linha.trim().startsWith("--")) {
                semComentarios.append(linha).append('\n');
            }
        }
        try (Statement st = c.createStatement()) {
            for (String comando : semComentarios.toString().split(";")) {
                if (!comando.isBlank()) {
                    st.execute(comando);
                }
            }
        }
    }

    private long contar(Connection c, String tabela) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    @Test
    void dadosESequencePermanecemAposReabrirOArquivo() throws Exception {
        try (Connection c = abrir(); Statement st = c.createStatement()) {
            aplicarSchema(c);
            st.executeUpdate(INSERT_CLIENTE);
            st.executeUpdate("INSERT INTO ATENDIMENTO (CLIENTE_ID, DATA_ABERTURA, ASSUNTO, SITUACAO) "
                    + "VALUES (1, DATE '2026-11-24', 'Primeiro contato', 'A')");
        }

        try (Connection c = abrir(); Statement st = c.createStatement()) {
            aplicarSchema(c); // reexecutar não pode apagar nem duplicar nada
            assertThat(contar(c, "CLIENTE")).isEqualTo(1);
            assertThat(contar(c, "ATENDIMENTO")).isEqualTo(1);
            try (ResultSet rs = st.executeQuery("SELECT NEXT VALUE FOR SEQ_CLIENTE")) {
                rs.next();
                assertThat(rs.getLong(1)).isEqualTo(2L); // a sequence continua de onde parou
            }
        }
    }

    @Test
    void naoPermiteAtendimentoDeClienteInexistente() throws Exception {
        try (Connection c = abrir(); Statement st = c.createStatement()) {
            aplicarSchema(c);
            assertThatThrownBy(() -> st.executeUpdate("INSERT INTO ATENDIMENTO "
                    + "(CLIENTE_ID, DATA_ABERTURA, ASSUNTO, SITUACAO) VALUES (999, DATE '2026-11-24', 'X', 'A')"))
                    .isInstanceOf(SQLException.class);
        }
    }

    @Test
    void naoPermiteExcluirClienteComAtendimento() throws Exception {
        try (Connection c = abrir(); Statement st = c.createStatement()) {
            aplicarSchema(c);
            st.executeUpdate(INSERT_CLIENTE);
            st.executeUpdate("INSERT INTO ATENDIMENTO (CLIENTE_ID, DATA_ABERTURA, ASSUNTO, SITUACAO) "
                    + "VALUES (1, DATE '2026-11-24', 'X', 'A')");
            assertThatThrownBy(() -> st.executeUpdate("DELETE FROM CLIENTE WHERE ID = 1"))
                    .isInstanceOf(SQLException.class);
        }
    }

    @Test
    void camposObrigatoriosDoLegadoContinuamNotNull() throws Exception {
        try (Connection c = abrir(); Statement st = c.createStatement()) {
            aplicarSchema(c);
            // EMAIL e TELEFONE são NOT NULL no cliente.sql, mesmo sem "required" no formulário.
            assertThatThrownBy(() -> st.executeUpdate("INSERT INTO CLIENTE "
                    + "(NOME, TIPO_PESSOA, CPF_CNPJ, SITUACAO, CEP, ESTADO) "
                    + "VALUES ('Joao da Silva', 'F', '52998224725', 'A', '89280000', 'SC')"))
                    .isInstanceOf(SQLException.class);
        }
    }
}
