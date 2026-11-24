package cliente;

public final class ValidadorDocumento {

    private ValidadorDocumento() {
    }

    public static String validar(String tipo, String documento) {
        String codigo = Texto.de(tipo).toUpperCase();
        if (codigo.length() == 0) {
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

    /** Nao cobra o documento enquanto o campo ainda estiver vazio. */
    public static String revisar(String tipo, String documento) {
        if (Texto.de(documento).length() == 0) {
            return null;
        }
        return validar(tipo, documento);
    }
}
