package cliente;

public final class ValidadorCep {

    private ValidadorCep() {
    }

    public static String validar(String cep) {
        String texto = Texto.de(cep);
        if (texto.length() == 0) {
            return "Informe o CEP.";
        }
        if (Texto.temLetra(texto)) {
            return "CEP deve conter apenas números. A máscara 00000-000 é opcional.";
        }
        String semEspaco = texto.replace(" ", "");
        if (semEspaco.indexOf('-') >= 0) {
            if (!semEspaco.matches("\\d{5}-\\d{3}")) {
                return "CEP inválido. Use 00000000 ou 00000-000.";
            }
        } else if (!semEspaco.matches("\\d{8}")) {
            return "CEP inválido. Use 00000000 ou 00000-000.";
        }

        String digitos = Texto.apenasDigitos(semEspaco);
        if ("00000000".equals(digitos) || Texto.todosIguais(digitos)) {
            return "CEP inválido.";
        }
        return null;
    }

    /**
     * Confere a faixa geral dos Correios. Retorna null se o CEP ou a UF
     * ainda nao puderem ser cruzados.
     */
    public static String compativelComUf(String cep, String estado) {
        if (validar(cep) != null || ValidadorUf.sigla(estado) == null) {
            return null;
        }
        String sigla = ValidadorUf.sigla(estado);
        int numero = Integer.parseInt(Texto.apenasDigitos(cep));
        if (!pertence(sigla, numero)) {
            return "O CEP não pertence à faixa da UF " + sigla + ".";
        }
        return null;
    }

    private static boolean pertence(String uf, int cep) {
        if ("AC".equals(uf)) return entre(cep, 69900000, 69999999);
        if ("AL".equals(uf)) return entre(cep, 57000000, 57999999);
        if ("AM".equals(uf)) return entre(cep, 69000000, 69299999) || entre(cep, 69400000, 69899999);
        if ("AP".equals(uf)) return entre(cep, 68900000, 68999999);
        if ("BA".equals(uf)) return entre(cep, 40000000, 48999999);
        if ("CE".equals(uf)) return entre(cep, 60000000, 63999999);
        if ("DF".equals(uf)) return entre(cep, 70000000, 72799999) || entre(cep, 73000000, 73699999);
        if ("ES".equals(uf)) return entre(cep, 29000000, 29999999);
        if ("GO".equals(uf)) return entre(cep, 72800000, 72999999) || entre(cep, 73700000, 76799999);
        if ("MA".equals(uf)) return entre(cep, 65000000, 65999999);
        if ("MG".equals(uf)) return entre(cep, 30000000, 39999999);
        if ("MS".equals(uf)) return entre(cep, 79000000, 79999999);
        if ("MT".equals(uf)) return entre(cep, 78000000, 78899999);
        if ("PA".equals(uf)) return entre(cep, 66000000, 68899999);
        if ("PB".equals(uf)) return entre(cep, 58000000, 58999999);
        if ("PE".equals(uf)) return entre(cep, 50000000, 56999999);
        if ("PI".equals(uf)) return entre(cep, 64000000, 64999999);
        if ("PR".equals(uf)) return entre(cep, 80000000, 87999999);
        if ("RJ".equals(uf)) return entre(cep, 20000000, 28999999);
        if ("RN".equals(uf)) return entre(cep, 59000000, 59999999);
        if ("RO".equals(uf)) return entre(cep, 76800000, 76999999) || entre(cep, 78900000, 78999999);
        if ("RR".equals(uf)) return entre(cep, 69300000, 69399999);
        if ("RS".equals(uf)) return entre(cep, 90000000, 99999999);
        if ("SC".equals(uf)) return entre(cep, 88000000, 89999999);
        if ("SE".equals(uf)) return entre(cep, 49000000, 49999999);
        if ("SP".equals(uf)) return entre(cep, 1000000, 19999999);
        if ("TO".equals(uf)) return entre(cep, 77000000, 77999999);
        return false;
    }

    private static boolean entre(int cep, int inicio, int fim) {
        return cep >= inicio && cep <= fim;
    }
}
