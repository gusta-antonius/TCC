package cliente;

public final class ValidadorUf {

    private static final String[] SIGLAS = {
        "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA",
        "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN",
        "RO", "RR", "RS", "SC", "SE", "SP", "TO"
    };

    private ValidadorUf() {
    }

    public static String sigla(String estado) {
        String texto = Texto.de(estado).toUpperCase();
        if (texto.length() != 2) {
            return null;
        }
        for (int i = 0; i < SIGLAS.length; i++) {
            if (SIGLAS[i].equals(texto)) {
                return texto;
            }
        }
        return null;
    }

    public static String validar(String estado) {
        String texto = Texto.de(estado);
        if (texto.length() == 0) {
            return "Informe a UF.";
        }
        if (sigla(texto) == null) {
            return "UF inválida. Use a sigla do estado, por exemplo SP.";
        }
        return null;
    }
}
