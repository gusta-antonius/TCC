package cliente;

public final class Texto {

    private Texto() {
    }

    public static String de(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.trim();
        if ("null".equalsIgnoreCase(texto)) {
            return "";
        }
        return texto;
    }

    public static String apenasDigitos(String valor) {
        String texto = de(valor);
        StringBuilder digitos = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);
            if (caractere >= '0' && caractere <= '9') {
                digitos.append(caractere);
            }
        }
        return digitos.toString();
    }

    public static boolean temLetra(String valor) {
        String texto = de(valor);
        for (int i = 0; i < texto.length(); i++) {
            if (Character.isLetter(texto.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean todosIguais(String digitos) {
        if (digitos == null || digitos.length() == 0) {
            return false;
        }
        char primeiro = digitos.charAt(0);
        for (int i = 1; i < digitos.length(); i++) {
            if (digitos.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }
}
