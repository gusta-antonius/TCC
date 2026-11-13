package atendimento;

import cliente.Texto;

public final class ValidadorAssunto {

    private static final int TAMANHO_MAXIMO = 60;

    private ValidadorAssunto() {
    }

    public static String validar(String assunto) {
        String texto = Texto.de(assunto);
        if (texto.length() == 0) {
            return "Informe o assunto.";
        }
        if (texto.length() > TAMANHO_MAXIMO) {
            return "Assunto deve ter no máximo " + TAMANHO_MAXIMO + " caracteres.";
        }
        return null;
    }
}
