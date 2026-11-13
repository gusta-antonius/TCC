package cliente;

public final class ValidadorTelefone {

    private ValidadorTelefone() {
    }

    public static String validar(String telefone) {
        String texto = Texto.de(telefone);
        if (texto.length() == 0) {
            return "Informe o telefone.";
        }
        if (Texto.temLetra(texto)) {
            return "Telefone deve conter apenas números. A máscara é opcional.";
        }
        String digitos = normalizar(texto);
        if (digitos.length() != 10 && digitos.length() != 11) {
            return "Telefone inválido. Informe DDD + número, com 10 ou 11 dígitos.";
        }
        if (Texto.todosIguais(digitos)) {
            return "Telefone inválido.";
        }

        int ddd = Integer.parseInt(digitos.substring(0, 2));
        if (!dddExiste(ddd)) {
            return "DDD inválido.";
        }

        char inicioAssinante = digitos.charAt(2);
        if (digitos.length() == 11) {
            if (inicioAssinante != '9') {
                return "Celular deve começar com 9 depois do DDD.";
            }
        } else if (inicioAssinante < '2' || inicioAssinante > '5') {
            return "Telefone fixo deve começar com 2, 3, 4 ou 5 depois do DDD.";
        }
        return null;
    }

    public static String compativelComUf(String telefone, String estado) {
        if (validar(telefone) != null || ValidadorUf.sigla(estado) == null) {
            return null;
        }
        String digitos = normalizar(telefone);
        int ddd = Integer.parseInt(digitos.substring(0, 2));
        String sigla = ValidadorUf.sigla(estado);
        if (!dddDaUf(sigla, ddd)) {
            return "O DDD " + ddd + " não corresponde à UF " + sigla + ".";
        }
        return null;
    }

    private static String normalizar(String telefone) {
        String digitos = Texto.apenasDigitos(telefone);
        if ((digitos.length() == 12 || digitos.length() == 13) && digitos.startsWith("55")) {
            return digitos.substring(2);
        }
        return digitos;
    }

    private static boolean dddExiste(int ddd) {
        return dddDaUf("AC", ddd) || dddDaUf("AL", ddd) || dddDaUf("AM", ddd) || dddDaUf("AP", ddd)
                || dddDaUf("BA", ddd) || dddDaUf("CE", ddd) || dddDaUf("DF", ddd) || dddDaUf("ES", ddd)
                || dddDaUf("GO", ddd) || dddDaUf("MA", ddd) || dddDaUf("MG", ddd) || dddDaUf("MS", ddd)
                || dddDaUf("MT", ddd) || dddDaUf("PA", ddd) || dddDaUf("PB", ddd) || dddDaUf("PE", ddd)
                || dddDaUf("PI", ddd) || dddDaUf("PR", ddd) || dddDaUf("RJ", ddd) || dddDaUf("RN", ddd)
                || dddDaUf("RO", ddd) || dddDaUf("RR", ddd) || dddDaUf("RS", ddd) || dddDaUf("SC", ddd)
                || dddDaUf("SE", ddd) || dddDaUf("SP", ddd) || dddDaUf("TO", ddd);
    }

    private static boolean dddDaUf(String uf, int ddd) {
        if ("SP".equals(uf)) return ddd >= 11 && ddd <= 19;
        if ("RJ".equals(uf)) return ddd == 21 || ddd == 22 || ddd == 24;
        if ("ES".equals(uf)) return ddd == 27 || ddd == 28;
        if ("MG".equals(uf)) return (ddd >= 31 && ddd <= 35) || ddd == 37 || ddd == 38;
        if ("PR".equals(uf)) return ddd >= 41 && ddd <= 46;
        if ("SC".equals(uf)) return ddd >= 47 && ddd <= 49;
        if ("RS".equals(uf)) return ddd == 51 || ddd == 53 || ddd == 54 || ddd == 55;
        if ("DF".equals(uf)) return ddd == 61;
        if ("GO".equals(uf)) return ddd == 61 || ddd == 62 || ddd == 64;
        if ("TO".equals(uf)) return ddd == 63;
        if ("MT".equals(uf)) return ddd == 65 || ddd == 66;
        if ("MS".equals(uf)) return ddd == 67;
        if ("AC".equals(uf)) return ddd == 68;
        if ("RO".equals(uf)) return ddd == 69;
        if ("BA".equals(uf)) return ddd == 71 || ddd == 73 || ddd == 74 || ddd == 75 || ddd == 77;
        if ("SE".equals(uf)) return ddd == 79;
        if ("PE".equals(uf)) return ddd == 81 || ddd == 87;
        if ("AL".equals(uf)) return ddd == 82;
        if ("PB".equals(uf)) return ddd == 83;
        if ("RN".equals(uf)) return ddd == 84;
        if ("CE".equals(uf)) return ddd == 85 || ddd == 88;
        if ("PI".equals(uf)) return ddd == 86 || ddd == 89;
        if ("PA".equals(uf)) return ddd == 91 || ddd == 93 || ddd == 94;
        if ("AM".equals(uf)) return ddd == 92 || ddd == 97;
        if ("RR".equals(uf)) return ddd == 95;
        if ("AP".equals(uf)) return ddd == 96;
        if ("MA".equals(uf)) return ddd == 98 || ddd == 99;
        return false;
    }
}
