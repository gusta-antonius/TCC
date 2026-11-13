package atendimento;

import cliente.Texto;

public final class ValidadorDataAbertura {

    private ValidadorDataAbertura() {
    }

    public static String validar(String data) {
        String texto = Texto.de(data);
        if (texto.length() == 0) {
            return "Informe a data de abertura.";
        }
        if (!dataValida(texto)) {
            return "Data de abertura inválida. Use dd/mm/aaaa.";
        }
        return null;
    }

    private static boolean dataValida(String texto) {
        if (texto.length() != 10 || texto.charAt(2) != '/' || texto.charAt(5) != '/') {
            return false;
        }
        int dia = numero(texto.substring(0, 2));
        int mes = numero(texto.substring(3, 5));
        int ano = numero(texto.substring(6, 10));
        if (dia < 1 || mes < 1 || mes > 12 || ano < 1900 || ano > 2100) {
            return false;
        }
        return dia <= diasNoMes(mes, ano);
    }

    private static int numero(String texto) {
        if (texto.length() == 0) {
            return -1;
        }
        int valor = 0;
        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);
            if (caractere < '0' || caractere > '9') {
                return -1;
            }
            valor = valor * 10 + (caractere - '0');
        }
        return valor;
    }

    private static int diasNoMes(int mes, int ano) {
        if (mes == 2) {
            if (ano % 400 == 0 || (ano % 4 == 0 && ano % 100 != 0)) {
                return 29;
            }
            return 28;
        }
        if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
            return 30;
        }
        return 31;
    }
}
