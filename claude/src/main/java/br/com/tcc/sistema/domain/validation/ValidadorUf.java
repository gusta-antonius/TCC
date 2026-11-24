package br.com.tcc.sistema.domain.validation;

/**
 * Validador de UF - migrado de cliente.ValidadorUf do sistema legado.
 * Preserva lista de siglas e mensagens originais integralmente.
 */
public final class ValidadorUf {

    private static final String[] SIGLAS = {
        "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA",
        "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN",
        "RO", "RR", "RS", "SC", "SE", "SP", "TO"
    };

    private ValidadorUf() {
    }

    public static String sigla(String estado) {
        String texto = TextoUtil.de(estado).toUpperCase();
        if (texto.length() != 2) {
            return null;
        }
        for (String s : SIGLAS) {
            if (s.equals(texto)) {
                return texto;
            }
        }
        return null;
    }

    public static String validar(String estado) {
        String texto = TextoUtil.de(estado);
        if (texto.isEmpty()) {
            return "Informe a UF.";
        }
        if (sigla(texto) == null) {
            return "UF inválida. Use a sigla do estado, por exemplo SP.";
        }
        return null;
    }
}
