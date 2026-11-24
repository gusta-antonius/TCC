package br.com.tcc.sistema.domain.validation;

/**
 * Validador do cliente vinculado ao atendimento - migrado de
 * atendimento.ValidadorClienteAtendimento do sistema legado.
 *
 * O parâmetro "status" de validarConsulta reproduz o session.status do Unify NXJ
 * após o EXEC SQL do legado: 0 = consulta ok, -35 = nenhum registro encontrado,
 * qualquer outro valor = falha na consulta.
 */
public final class ValidadorClienteAtendimento {

    public static final int STATUS_SUCESSO = 0;
    public static final int STATUS_NAO_ENCONTRADO = -35;

    private static final int TAMANHO_MAXIMO = 10;

    private ValidadorClienteAtendimento() {
    }

    public static String validarId(String clienteId) {
        String texto = TextoUtil.de(clienteId);
        if (texto.length() == 0) {
            return "Informe o cliente.";
        }
        if (!texto.equals(TextoUtil.apenasDigitos(texto))) {
            return "Cliente deve conter apenas números.";
        }
        if (texto.length() > TAMANHO_MAXIMO) {
            return "Cliente deve ter no máximo " + TAMANHO_MAXIMO + " dígitos.";
        }
        return null;
    }

    public static String validarConsulta(int status, String situacao) {
        if (status == STATUS_NAO_ENCONTRADO) {
            return "Cliente não encontrado.";
        }
        if (status != STATUS_SUCESSO) {
            return "Não foi possível consultar o cliente.";
        }
        String codigo = TextoUtil.de(situacao).toUpperCase();
        if ("A".equals(codigo)) {
            return null;
        }
        return "Cliente inativo.";
    }
}
