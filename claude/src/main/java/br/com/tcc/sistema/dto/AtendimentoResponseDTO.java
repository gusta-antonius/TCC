package br.com.tcc.sistema.dto;

import br.com.tcc.sistema.domain.entity.Atendimento;
import java.time.format.DateTimeFormatter;

public record AtendimentoResponseDTO(
        Long id,
        Long clienteId,
        String dataAbertura,
        String assunto,
        String descricao,
        String situacao) {

    /** Mesmo formato de exibição do campo data_abertura do formulário legado (dd/MM/yyyy). */
    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public static AtendimentoResponseDTO de(Atendimento a) {
        return new AtendimentoResponseDTO(
                a.getId(), a.getCliente().getId(), FORMATO_DATA.format(a.getDataAbertura()),
                a.getAssunto(), a.getDescricao(), a.getSituacao());
    }
}
