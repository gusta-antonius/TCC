package cliente;

public final class ValidadorEmail {

    private static final int TAMANHO_MAXIMO = 48;

    private ValidadorEmail() {
    }

    public static String validar(String email) {
        String texto = Texto.de(email);
        if (texto.length() == 0) {
            return "Informe o e-mail.";
        }
        if (texto.length() > TAMANHO_MAXIMO) {
            return "E-mail deve ter no máximo " + TAMANHO_MAXIMO + " caracteres.";
        }
        if (texto.indexOf(' ') >= 0) {
            return "E-mail não pode conter espaços.";
        }

        int arroba = texto.indexOf('@');
        if (arroba <= 0 || arroba != texto.lastIndexOf('@') || arroba == texto.length() - 1) {
            return "E-mail inválido. Use o formato nome@dominio.com.";
        }

        String local = texto.substring(0, arroba);
        String dominio = texto.substring(arroba + 1);
        if (!localValido(local) || !dominioValido(dominio)) {
            return "E-mail inválido. Use o formato nome@dominio.com.";
        }
        return null;
    }

    private static boolean localValido(String local) {
        if (local.length() > 64) {
            return false;
        }
        if (local.charAt(0) == '.' || local.charAt(local.length() - 1) == '.') {
            return false;
        }
        if (local.indexOf("..") >= 0) {
            return false;
        }
        for (int i = 0; i < local.length(); i++) {
            char caractere = local.charAt(i);
            boolean permitido = (caractere >= 'A' && caractere <= 'Z')
                    || (caractere >= 'a' && caractere <= 'z')
                    || (caractere >= '0' && caractere <= '9')
                    || caractere == '.'
                    || caractere == '_'
                    || caractere == '%'
                    || caractere == '+'
                    || caractere == '-';
            if (!permitido) {
                return false;
            }
        }
        return true;
    }

    private static boolean dominioValido(String dominio) {
        if (dominio.length() < 3 || dominio.indexOf('.') < 0) {
            return false;
        }
        if (dominio.charAt(0) == '.' || dominio.charAt(dominio.length() - 1) == '.') {
            return false;
        }
        if (dominio.indexOf("..") >= 0) {
            return false;
        }

        String[] partes = dominio.split("\\.");
        if (partes.length < 2) {
            return false;
        }
        for (int i = 0; i < partes.length; i++) {
            if (!rotuloValido(partes[i])) {
                return false;
            }
        }
        String tld = partes[partes.length - 1];
        if (tld.length() < 2) {
            return false;
        }
        for (int i = 0; i < tld.length(); i++) {
            char caractere = tld.charAt(i);
            boolean letra = (caractere >= 'A' && caractere <= 'Z')
                    || (caractere >= 'a' && caractere <= 'z');
            if (!letra) {
                return false;
            }
        }
        return true;
    }

    private static boolean rotuloValido(String rotulo) {
        if (rotulo.length() == 0 || rotulo.length() > 63) {
            return false;
        }
        if (rotulo.charAt(0) == '-' || rotulo.charAt(rotulo.length() - 1) == '-') {
            return false;
        }
        for (int i = 0; i < rotulo.length(); i++) {
            char caractere = rotulo.charAt(i);
            boolean permitido = (caractere >= 'A' && caractere <= 'Z')
                    || (caractere >= 'a' && caractere <= 'z')
                    || (caractere >= '0' && caractere <= '9')
                    || caractere == '-';
            if (!permitido) {
                return false;
            }
        }
        return true;
    }
}
