package cliente;

public final class ValidadorCpf {

    private ValidadorCpf() {
    }

    public static String validar(String cpf) {
        String texto = Texto.de(cpf);
        if (texto.length() == 0) {
            return "Informe o CPF.";
        }
        if (Texto.temLetra(texto)) {
            return "CPF deve conter apenas números. A pontuação é opcional.";
        }
        String digitos = Texto.apenasDigitos(texto);
        if (digitos.length() != 11) {
            return "CPF deve ter 11 dígitos.";
        }
        if (Texto.todosIguais(digitos)) {
            return "CPF inválido.";
        }
        int primeiro = digito(digitos.substring(0, 9));
        int segundo = digito(digitos.substring(0, 10));
        if (primeiro != digitos.charAt(9) - '0' || segundo != digitos.charAt(10) - '0') {
            return "CPF inválido. O dígito verificador não confere.";
        }
        return null;
    }

    private static int digito(String base) {
        int soma = 0;
        int peso = base.length() + 1;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * peso;
            peso--;
        }
        int resto = soma % 11;
        if (resto < 2) {
            return 0;
        }
        return 11 - resto;
    }
}
