package br.com.tcc.sistema.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentoTest {

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "529.982.247-25", " 529.982.247-25 ", "111.444.777-35"})
    void cpfValidoComOuSemPontuacao(String cpf) {
        assertThat(ValidadorCpf.validar(cpf)).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe o CPF.",
            "null|Informe o CPF.",
            "5299822472A|CPF deve conter apenas números. A pontuação é opcional.",
            "1234567890|CPF deve ter 11 dígitos.",
            "11111111111|CPF inválido.",
            "52998224726|CPF inválido. O dígito verificador não confere."})
    void cpfInvalido(String cpf, String mensagem) {
        assertThat(ValidadorCpf.validar(cpf)).isEqualTo(mensagem);
    }

    @ParameterizedTest
    @ValueSource(strings = {"11222333000181", "11.222.333/0001-81"})
    void cnpjValido(String cnpj) {
        assertThat(ValidadorCnpj.validar(cnpj)).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "|Informe o CNPJ.",
            "1122233300018X|CNPJ deve conter apenas números. A pontuação é opcional.",
            "1122233300018|CNPJ deve ter 14 dígitos.",
            "00000000000000|CNPJ inválido.",
            "11222333000182|CNPJ inválido. O dígito verificador não confere."})
    void cnpjInvalido(String cnpj, String mensagem) {
        assertThat(ValidadorCnpj.validar(cnpj)).isEqualTo(mensagem);
    }

    @Test
    void documentoSegueOTipoDePessoa() {
        assertThat(ValidadorDocumento.validar("F", "52998224725")).isNull();
        assertThat(ValidadorDocumento.validar("f", "52998224725")).isNull();
        assertThat(ValidadorDocumento.validar("J", "11222333000181")).isNull();
        assertThat(ValidadorDocumento.validar("J", "52998224725")).isEqualTo("CNPJ deve ter 14 dígitos.");
        assertThat(ValidadorDocumento.validar("", "52998224725"))
                .isEqualTo("Informe o tipo (F ou J) antes do CPF/CNPJ.");
        assertThat(ValidadorDocumento.validar("X", "52998224725"))
                .isEqualTo("Tipo de pessoa inválido. Use F ou J para validar o documento.");
    }

    @Test
    void revisarNaoCobraDocumentoVazio() {
        assertThat(ValidadorDocumento.revisar("F", "")).isNull();
        assertThat(ValidadorDocumento.revisar("F", "123")).isEqualTo("CPF deve ter 11 dígitos.");
    }
}
