package br.com.tcc.sistema.domain.validation;

/**
 * Validador de e-mail - migrado de cliente.ValidadorEmail do sistema legado.
 * Preserva algoritmo, regras e mensagens de erro originais integralmente.
 *
 * Tamanho máximo: 48 caracteres (conforme campo EMAIL VARCHAR(48) no banco).
 * Regras: sem espaços, um único '@', local e domínio válidos conforme lógica original.
 */
public final class ValidadorEmail {

    private static final int TAMANHO_MAXIMO = 48;

    private ValidadorEmail() {
    }

    public static String validar(String email) {
        String texto = TextoUtil.de(email);
        if (texto.isEmpty()) {
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
        if (local.contains("..")) {
            return false;
        }
        for (int i = 0; i < local.length(); i++) {
            char c = local.charAt(i);
            boolean permitido = (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9')
                    || c == '.' || c == '_' || c == '%' || c == '+' || c == '-';
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
        if (dominio.contains("..")) {
            return false;
        }
        String[] partes = dominio.split("\\.");
        if (partes.length < 2) {
            return false;
        }
        for (String parte : partes) {
            if (!rotuloValido(parte)) {
                return false;
            }
        }
        String tld = partes[partes.length - 1];
        if (tld.length() < 2) {
            return false;
        }
        for (int i = 0; i < tld.length(); i++) {
            char c = tld.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                return false;
            }
        }
        return true;
    }

    private static boolean rotuloValido(String rotulo) {
        if (rotulo.isEmpty() || rotulo.length() > 63) {
            return false;
        }
        if (rotulo.charAt(0) == '-' || rotulo.charAt(rotulo.length() - 1) == '-') {
            return false;
        }
        for (int i = 0; i < rotulo.length(); i++) {
            char c = rotulo.charAt(i);
            boolean permitido = (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9')
                    || c == '-';
            if (!permitido) {
                return false;
            }
        }
        return true;
    }
}
