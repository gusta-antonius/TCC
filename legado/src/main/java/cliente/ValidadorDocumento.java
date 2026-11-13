package cliente;

public final class ValidadorDocumento {

    private ValidadorDocumento() {
    }

    public static String validar(String tipo, String documento) {
        String codigo = ValidadorTipoPessoa.codigo(tipo);
        if (codigo == null) {
            if (Texto.de(tipo).length() == 0) {
                return "Informe o tipo (F ou J) antes do CPF/CNPJ.";
            }
            return "Tipo de pessoa inválido. Use F ou J para validar o documento.";
        }
        if ("F".equals(codigo)) {
            return ValidadorCpf.validar(documento);
        }
        return ValidadorCnpj.validar(documento);
    }

    /** Nao cobra o documento enquanto o campo ainda estiver vazio. */
    public static String revisar(String tipo, String documento) {
        if (Texto.de(documento).length() == 0) {
            return null;
        }
        return validar(tipo, documento);
    }
}
