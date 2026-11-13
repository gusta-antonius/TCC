package cliente;

public final class ValidadorTipoPessoa {

    private ValidadorTipoPessoa() {
    }

    public static String codigo(String tipo) {
        String texto = Texto.de(tipo).toUpperCase();
        if ("F".equals(texto)) {
            return "F";
        }
        if ("J".equals(texto)) {
            return "J";
        }
        return null;
    }

    public static String validar(String tipo) {
        if (Texto.de(tipo).length() == 0) {
            return "Informe o tipo de pessoa: F (física) ou J (jurídica).";
        }
        if (codigo(tipo) == null) {
            return "Tipo inválido. Use F (física) ou J (jurídica).";
        }
        return null;
    }
}
