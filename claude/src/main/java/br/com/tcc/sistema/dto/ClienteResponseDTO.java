package br.com.tcc.sistema.dto;

import br.com.tcc.sistema.domain.entity.Cliente;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String tipoPessoa,
        String cpfCnpj,
        String situacao,
        String telefone,
        String email,
        String cep,
        String estado,
        String cidade,
        String bairro,
        String logradouro,
        String numero) {

    public static ClienteResponseDTO de(Cliente c) {
        return new ClienteResponseDTO(
                c.getId(), c.getNome(), c.getTipoPessoa(), c.getCpfCnpj(), c.getSituacao(),
                c.getTelefone(), c.getEmail(), c.getCep(), c.getEstado(), c.getCidade(),
                c.getBairro(), c.getLogradouro(), c.getNumero());
    }
}
