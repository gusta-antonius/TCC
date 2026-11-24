package br.com.tcc.sistema.exception;

/** Operação bloqueada por restrição de integridade (FK do banco legado). */
public class ConflitoNegocioException extends RuntimeException {

    public ConflitoNegocioException(String mensagem) {
        super(mensagem);
    }
}
