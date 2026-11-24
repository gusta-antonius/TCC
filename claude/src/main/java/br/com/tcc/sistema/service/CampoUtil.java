package br.com.tcc.sistema.service;

import br.com.tcc.sistema.exception.ValidacaoNegocioException;

/** Funções comuns às validações de gravação dos services. */
final class CampoUtil {

    private CampoUtil() {
    }

    /** Equivale a texto(NullableString) dos formulários legado: nulo vira "" e o valor é aparado. */
    static String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    /** Campo opcional vazio é gravado como NULL (comportamento de um campo NXJ vazio). */
    static String nuloSeVazio(String valor) {
        String t = texto(valor);
        return t.isEmpty() ? null : t;
    }

    /**
     * Equivale a invalido(mensagem) + queueNextField(campo): se o validador legado retornou
     * mensagem, interrompe a gravação indicando o campo.
     */
    static void verificar(String campo, String mensagem) {
        if (mensagem != null) {
            throw new ValidacaoNegocioException(campo, mensagem);
        }
    }

    static void verificarTamanho(String campo, String rotulo, String valor, int maximo) {
        if (valor.length() > maximo) {
            throw new ValidacaoNegocioException(campo, rotulo + " deve ter no máximo " + maximo + " caracteres.");
        }
    }
}
