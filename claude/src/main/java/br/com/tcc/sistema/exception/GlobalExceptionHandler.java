package br.com.tcc.sistema.exception;

import br.com.tcc.sistema.dto.ErroResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Traduz exceções em respostas HTTP sem expor detalhes internos (stack trace, SQL).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidacaoNegocioException.class)
    public ResponseEntity<ErroResponseDTO> validacao(ValidacaoNegocioException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponseDTO(e.getMessage(), e.getCampo()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> naoEncontrado(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponseDTO(e.getMessage(), null));
    }

    @ExceptionHandler(ConflitoNegocioException.class)
    public ResponseEntity<ErroResponseDTO> conflito(ConflitoNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponseDTO(e.getMessage(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> corpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponseDTO("Corpo da requisição inválido.", null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> idInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponseDTO("Identificador inválido. Informe apenas números.", e.getName()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErroResponseDTO> banco(DataAccessException e) {
        LOG.error("Falha de acesso ao banco de dados", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponseDTO("Não foi possível acessar o banco de dados.", null));
    }
}
