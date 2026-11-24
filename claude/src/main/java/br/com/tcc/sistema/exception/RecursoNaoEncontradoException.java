package br.com.tcc.sistema.exception;

/** Registro inexistente (no legado, consulta com session.status = -35). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
