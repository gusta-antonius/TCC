package atendimento;

import cliente.Texto;

public final class ValidadorClienteAtendimento {

    private static final int TAMANHO_MAXIMO = 10;

    private ValidadorClienteAtendimento() {
    }

    public static String validarId(String clienteId) {
        String texto = Texto.de(clienteId);
        if (texto.length() == 0) {
            return "Informe o cliente.";
        }
        if (!texto.equals(Texto.apenasDigitos(texto))) {
            return "Cliente deve conter apenas números.";
        }
        if (texto.length() > TAMANHO_MAXIMO) {
            return "Cliente deve ter no máximo " + TAMANHO_MAXIMO + " dígitos.";
        }
        return null;
    }

    public static String validarConsulta(int status, String situacao) {
        if (status == -35) {
            return "Cliente não encontrado.";
        }
        if (status != 0) {
            return "Não foi possível consultar o cliente.";
        }
        String codigo = Texto.de(situacao).toUpperCase();
        if ("A".equals(codigo)) {
            return null;
        }
        return "Cliente inativo.";
    }
}
