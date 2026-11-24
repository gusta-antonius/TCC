package br.com.tcc.sistema.exception;

/** Regra de negócio/validação violada (equivale a displayToMessageBox + rejectOperation do legado). */
public class ValidacaoNegocioException extends RuntimeException {

    private final String campo;

    public ValidacaoNegocioException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
