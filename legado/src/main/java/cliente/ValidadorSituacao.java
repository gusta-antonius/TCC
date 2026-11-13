package cliente;

public final class ValidadorSituacao {

    private ValidadorSituacao() {
    }

    public static String validar(String situacao) {
        String texto = Texto.de(situacao).toUpperCase();
        if (texto.length() == 0) {
            return "Informe a situação: A (ativo) ou I (inativo).";
        }
        if ("A".equals(texto) || "I".equals(texto)) {
            return null;
        }
        return "Situação inválida. Use A (ativo) ou I (inativo).";
    }
}
