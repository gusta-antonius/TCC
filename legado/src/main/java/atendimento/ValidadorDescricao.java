package atendimento;

import cliente.Texto;

public final class ValidadorDescricao {

    private static final int TAMANHO_MAXIMO = 200;

    private ValidadorDescricao() {
    }

    public static String validar(String descricao) {
        String texto = Texto.de(descricao);
        if (texto.length() == 0) {
            return null;
        }
        if (texto.length() > TAMANHO_MAXIMO) {
            return "Descrição deve ter no máximo " + TAMANHO_MAXIMO + " caracteres.";
        }
        return null;
    }
}
