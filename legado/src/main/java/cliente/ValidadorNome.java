package cliente;

public final class ValidadorNome {

    private static final int TAMANHO_MAXIMO = 60;

    private ValidadorNome() {
    }

    public static String validar(String nome, String tipo) {
        String texto = Texto.de(nome);
        if (texto.length() == 0) {
            return "Informe o nome.";
        }
        if (texto.length() > TAMANHO_MAXIMO) {
            return "Nome deve ter no máximo " + TAMANHO_MAXIMO + " caracteres.";
        }
        if (!Texto.temLetra(texto)) {
            return "Nome deve conter ao menos uma letra.";
        }

        String codigo = ValidadorTipoPessoa.codigo(tipo);
        if ("F".equals(codigo)) {
            return validarPessoaFisica(texto);
        }
        if ("J".equals(codigo)) {
            return validarPessoaJuridica(texto);
        }
        if (texto.length() < 2) {
            return "Nome deve ter ao menos 2 caracteres.";
        }
        return null;
    }

    /** Nao cobra o nome enquanto o campo ainda estiver vazio. */
    public static String revisar(String nome, String tipo) {
        if (Texto.de(nome).length() == 0) {
            return null;
        }
        return validar(nome, tipo);
    }

    private static String validarPessoaFisica(String nome) {
        String[] palavras = nome.split("\\s+");
        if (palavras.length < 2) {
            return "Pessoa física deve ter nome e sobrenome.";
        }
        boolean temPalavraUtil = false;
        for (int i = 0; i < palavras.length; i++) {
            if (!palavraFisica(palavras[i])) {
                return "Nome de pessoa física aceita apenas letras, espaço, apóstrofo, ponto e hífen.";
            }
            if (palavras[i].length() >= 2) {
                temPalavraUtil = true;
            }
        }
        if (!temPalavraUtil) {
            return "Informe nome e sobrenome.";
        }
        return null;
    }

    private static String validarPessoaJuridica(String nome) {
        if (nome.length() < 2) {
            return "Nome deve ter ao menos 2 caracteres.";
        }
        for (int i = 0; i < nome.length(); i++) {
            char caractere = nome.charAt(i);
            boolean permitido = Character.isLetterOrDigit(caractere)
                    || caractere == ' '
                    || caractere == '\''
                    || caractere == '-'
                    || caractere == '.'
                    || caractere == '&'
                    || caractere == '/';
            if (!permitido) {
                return "Nome da pessoa jurídica contém um caractere inválido.";
            }
        }
        return null;
    }

    private static boolean palavraFisica(String palavra) {
        if (palavra.length() == 0) {
            return false;
        }
        boolean temLetra = false;
        for (int i = 0; i < palavra.length(); i++) {
            char caractere = palavra.charAt(i);
            if (Character.isLetter(caractere)) {
                temLetra = true;
                continue;
            }
            if (caractere != '\'' && caractere != '-' && caractere != '.') {
                return false;
            }
        }
        return temLetra;
    }
}
