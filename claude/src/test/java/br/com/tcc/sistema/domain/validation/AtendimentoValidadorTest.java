package br.com.tcc.sistema.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class AtendimentoValidadorTest {

    @ParameterizedTest
    @ValueSource(strings = {"24/11/2026", "29/02/2024", "01/01/1900", "31/12/2100"})
    void dataValida(String data) {
        assertThat(ValidadorDataAbertura.validar(data)).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe a data de abertura.",
            "2026-11-24|Data de abertura inválida. Use dd/mm/aaaa.",
            "24/11/26|Data de abertura inválida. Use dd/mm/aaaa.",
            "29/02/2025|Data de abertura inválida. Use dd/mm/aaaa.",
            "31/04/2026|Data de abertura inválida. Use dd/mm/aaaa.",
            "00/10/2026|Data de abertura inválida. Use dd/mm/aaaa.",
            "10/13/2026|Data de abertura inválida. Use dd/mm/aaaa.",
            "31/12/1899|Data de abertura inválida. Use dd/mm/aaaa.",
            "01/01/2101|Data de abertura inválida. Use dd/mm/aaaa.",
            "aa/bb/cccc|Data de abertura inválida. Use dd/mm/aaaa."})
    void dataInvalida(String data, String mensagem) {
        assertThat(ValidadorDataAbertura.validar(data)).isEqualTo(mensagem);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe o cliente.",
            "12a|Cliente deve conter apenas números.",
            "12345678901|Cliente deve ter no máximo 10 dígitos."})
    void idDoClienteInvalido(String id, String mensagem) {
        assertThat(ValidadorClienteAtendimento.validarId(id)).isEqualTo(mensagem);
    }

    @Test
    void idDoClienteValido() {
        assertThat(ValidadorClienteAtendimento.validarId("1")).isNull();
        assertThat(ValidadorClienteAtendimento.validarId("9999999999")).isNull();
    }

    @Test
    void apenasClienteAtivoPodeReceberAtendimento() {
        assertThat(ValidadorClienteAtendimento.validarConsulta(0, "A")).isNull();
        assertThat(ValidadorClienteAtendimento.validarConsulta(0, "a")).isNull();
        assertThat(ValidadorClienteAtendimento.validarConsulta(0, "I")).isEqualTo("Cliente inativo.");
        assertThat(ValidadorClienteAtendimento.validarConsulta(-35, null)).isEqualTo("Cliente não encontrado.");
        assertThat(ValidadorClienteAtendimento.validarConsulta(-1, null))
                .isEqualTo("Não foi possível consultar o cliente.");
    }
}
