package br.com.tcc.sistema.dto;

/**
 * Dados de entrada do cadastro de atendimento (campos do formulário CadastroAtendimento).
 *
 * clienteId e dataAbertura são String porque o legado valida o texto digitado
 * ("Cliente deve conter apenas números.", data no formato dd/mm/aaaa).
 */
public record AtendimentoRequestDTO(
        String clienteId,
        String dataAbertura,
        String assunto,
        String descricao,
        String situacao) {
}
