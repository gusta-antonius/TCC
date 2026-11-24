package br.com.tcc.sistema.dto;

/**
 * Corpo de resposta de erro.
 *
 * mensagem: texto que o legado exibia em session.displayToMessageBox.
 * campo:    campo que o legado reposicionava com session.queueNextField (pode ser null).
 */
public record ErroResponseDTO(String mensagem, String campo) {
}
