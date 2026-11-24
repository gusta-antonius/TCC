package br.com.tcc.sistema.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class NomeEmailTest {

    @Test
    void pessoaFisicaExigeNomeESobrenome() {
        assertThat(ValidadorNome.validar("João da Silva", "F")).isNull();
        assertThat(ValidadorNome.validar("Ana D'Ávila-Souza", "f")).isNull();
        assertThat(ValidadorNome.validar("Maria", "F")).isEqualTo("Pessoa física deve ter nome e sobrenome.");
        assertThat(ValidadorNome.validar("Maria 2", "F"))
                .isEqualTo("Nome de pessoa física aceita apenas letras, espaço, apóstrofo, ponto e hífen.");
        assertThat(ValidadorNome.validar("A B", "F")).isEqualTo("Informe nome e sobrenome.");
    }

    @Test
    void pessoaJuridicaAceitaDigitosESimbolosPermitidos() {
        assertThat(ValidadorNome.validar("Padaria 24h & Cia Ltda.", "J")).isNull();
        assertThat(ValidadorNome.validar("Empresa #1", "J"))
                .isEqualTo("Nome da pessoa jurídica contém um caractere inválido.");
        assertThat(ValidadorNome.validar("1", "J")).isEqualTo("Nome deve conter ao menos uma letra.");
    }

    @Test
    void regrasGeraisDoNome() {
        assertThat(ValidadorNome.validar("", "F")).isEqualTo("Informe o nome.");
        assertThat(ValidadorNome.validar("x".repeat(61), "J")).isEqualTo("Nome deve ter no máximo 60 caracteres.");
        assertThat(ValidadorNome.validar("123", "J")).isEqualTo("Nome deve conter ao menos uma letra.");
        assertThat(ValidadorNome.revisar("", "F")).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"joao@exemplo.com", "a.b_c+d%e-f@sub.dominio.com.br", "x@y-z.co"})
    void emailValido(String email) {
        assertThat(ValidadorEmail.validar(email)).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe o e-mail.",
            "a b@c.com|E-mail não pode conter espaços.",
            "semarroba.com|E-mail inválido. Use o formato nome@dominio.com.",
            "a@@b.com|E-mail inválido. Use o formato nome@dominio.com.",
            "@b.com|E-mail inválido. Use o formato nome@dominio.com.",
            "a@b|E-mail inválido. Use o formato nome@dominio.com.",
            "a..b@c.com|E-mail inválido. Use o formato nome@dominio.com.",
            ".a@c.com|E-mail inválido. Use o formato nome@dominio.com.",
            "a@-c.com|E-mail inválido. Use o formato nome@dominio.com.",
            "a@c.c1|E-mail inválido. Use o formato nome@dominio.com."})
    void emailInvalido(String email, String mensagem) {
        assertThat(ValidadorEmail.validar(email)).isEqualTo(mensagem);
    }

    @Test
    void emailLimitadoAQuarentaEOitoCaracteres() {
        String longo = "a".repeat(40) + "@b.com"; // 46
        assertThat(ValidadorEmail.validar(longo)).isNull();
        assertThat(ValidadorEmail.validar("a" + longo + "xyz"))
                .isEqualTo("E-mail deve ter no máximo 48 caracteres.");
    }
}
