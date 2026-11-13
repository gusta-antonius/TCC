package atendimento;

import cliente.Texto;

public final class ValidadorSituacaoAtendimento {

    private ValidadorSituacaoAtendimento() {
    }

    public static String validar(String situacao) {
        String texto = Texto.de(situacao).toUpperCase();
        if (texto.length() == 0) {
            return "Informe a situação: A (aberto) ou E (encerrado).";
        }
        if ("A".equals(texto) || "E".equals(texto)) {
            return null;
        }
        return "Situação inválida. Use A (aberto) ou E (encerrado).";
    }
}
