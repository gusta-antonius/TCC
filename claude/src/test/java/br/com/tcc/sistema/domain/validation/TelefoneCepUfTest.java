package br.com.tcc.sistema.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TelefoneCepUfTest {

    @ParameterizedTest
    @ValueSource(strings = {"47999998888", "(47) 99999-8888", "4732221111", "+55 47 99999-8888", "5547999998888"})
    void telefoneValido(String telefone) {
        assertThat(ValidadorTelefone.validar(telefone)).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe o telefone.",
            "4799999888A|Telefone deve conter apenas números. A máscara é opcional.",
            "479999988|Telefone inválido. Informe DDD + número, com 10 ou 11 dígitos.",
            "11111111111|Telefone inválido.",
            "10999998888|DDD inválido.",
            "47899998888|Celular deve começar com 9 depois do DDD.",
            "4712345678|Telefone fixo deve começar com 2, 3, 4 ou 5 depois do DDD."})
    void telefoneInvalido(String telefone, String mensagem) {
        assertThat(ValidadorTelefone.validar(telefone)).isEqualTo(mensagem);
    }

    @Test
    void telefoneDeveCombinarComAUf() {
        assertThat(ValidadorTelefone.compativelComUf("47999998888", "SC")).isNull();
        assertThat(ValidadorTelefone.compativelComUf("47999998888", "sc")).isNull();
        assertThat(ValidadorTelefone.compativelComUf("47999998888", "SP"))
                .isEqualTo("O DDD 47 não corresponde à UF SP.");
        // Sem UF válida ou com telefone inválido a conferência cruzada não se aplica.
        assertThat(ValidadorTelefone.compativelComUf("47999998888", "")).isNull();
        assertThat(ValidadorTelefone.compativelComUf("123", "SP")).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"89280000", "89280-000", "89280 - 000"})
    void cepValido(String cep) {
        assertThat(ValidadorCep.validar(cep)).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe o CEP.",
            "8928000A|CEP deve conter apenas números. A máscara 00000-000 é opcional.",
            "8928000|CEP inválido. Use 00000000 ou 00000-000.",
            "892800-00|CEP inválido. Use 00000000 ou 00000-000.",
            "00000000|CEP inválido.",
            "88888888|CEP inválido."})
    void cepInvalido(String cep, String mensagem) {
        assertThat(ValidadorCep.validar(cep)).isEqualTo(mensagem);
    }

    @Test
    void cepDeveCombinarComAFaixaDaUf() {
        assertThat(ValidadorCep.compativelComUf("89280-000", "SC")).isNull();
        assertThat(ValidadorCep.compativelComUf("01001-000", "SP")).isNull();
        assertThat(ValidadorCep.compativelComUf("89280-000", "SP"))
                .isEqualTo("O CEP não pertence à faixa da UF SP.");
        assertThat(ValidadorCep.compativelComUf("89280-000", "XX")).isNull();
    }

    @Test
    void ufValidaSigla() {
        assertThat(ValidadorUf.validar("SC")).isNull();
        assertThat(ValidadorUf.validar("sc")).isNull();
        assertThat(ValidadorUf.validar("")).isEqualTo("Informe a UF.");
        assertThat(ValidadorUf.validar("XX")).isEqualTo("UF inválida. Use a sigla do estado, por exemplo SP.");
        assertThat(ValidadorUf.sigla(" rj ")).isEqualTo("RJ");
        assertThat(ValidadorUf.sigla("R")).isNull();
    }

    @Test
    void textoUtilTrataNuloELiteralNull() {
        assertThat(TextoUtil.de(null)).isEmpty();
        assertThat(TextoUtil.de("  NULL ")).isEmpty();
        assertThat(TextoUtil.de(" abc ")).isEqualTo("abc");
        assertThat(TextoUtil.apenasDigitos("(47) 9-1")).isEqualTo("4791");
        assertThat(TextoUtil.temLetra("12-3")).isFalse();
        assertThat(TextoUtil.todosIguais("1111")).isTrue();
        assertThat(TextoUtil.todosIguais("")).isFalse();
    }
}
