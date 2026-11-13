package cliente;

public final class ValidadorCnpj {

    private static final int[] PESO_PRIMEIRO = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESO_SEGUNDO = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private ValidadorCnpj() {
    }

    public static String validar(String cnpj) {
        String texto = Texto.de(cnpj);
        if (texto.length() == 0) {
            return "Informe o CNPJ.";
        }
        if (Texto.temLetra(texto)) {
            return "CNPJ deve conter apenas números. A pontuação é opcional.";
        }
        String digitos = Texto.apenasDigitos(texto);
        if (digitos.length() != 14) {
            return "CNPJ deve ter 14 dígitos.";
        }
        if (Texto.todosIguais(digitos)) {
            return "CNPJ inválido.";
        }
        int primeiro = digito(digitos.substring(0, 12), PESO_PRIMEIRO);
        int segundo = digito(digitos.substring(0, 13), PESO_SEGUNDO);
        if (primeiro != digitos.charAt(12) - '0' || segundo != digitos.charAt(13) - '0') {
            return "CNPJ inválido. O dígito verificador não confere.";
        }
        return null;
    }

    private static int digito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        if (resto < 2) {
            return 0;
        }
        return 11 - resto;
    }
}
