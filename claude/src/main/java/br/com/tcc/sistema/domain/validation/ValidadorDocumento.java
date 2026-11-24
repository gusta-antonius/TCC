package br.com.tcc.sistema.domain.validation;

/**
 * Validador de documento (CPF ou CNPJ) conforme tipo de pessoa.
 * Migrado de cliente.ValidadorDocumento do sistema legado.
 * Preserva comportamento original integralmente.
 *
 * TIPO_PESSOA: 'F' = Física → valida CPF
 *              'J' = Jurídica → valida CNPJ
 */
public final class ValidadorDocumento {

    private ValidadorDocumento() {
    }

    public static String validar(String tipo, String documento) {
        String codigo = TextoUtil.de(tipo).toUpperCase();
        if (codigo.isEmpty()) {
            return "Informe o tipo (F ou J) antes do CPF/CNPJ.";
        }
        if ("F".equals(codigo)) {
            return ValidadorCpf.validar(documento);
        }
        if ("J".equals(codigo)) {
            return ValidadorCnpj.validar(documento);
        }
        return "Tipo de pessoa inválido. Use F ou J para validar o documento.";
    }

    /** Não cobra o documento enquanto o campo ainda estiver vazio. */
    public static String revisar(String tipo, String documento) {
        if (TextoUtil.de(documento).isEmpty()) {
            return null;
        }
        return validar(tipo, documento);
    }
}
