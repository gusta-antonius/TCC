package br.com.tcc.sistema.dto;

/**
 * Dados de entrada do cadastro de cliente (campos do formulário CadastroCliente).
 *
 * Todos os campos são String, como nos campos de texto do formulário legado, para que
 * as validações originais (e suas mensagens) sejam aplicadas sobre o valor informado.
 * O ID não é recebido: é gerado pela sequence SEQ_CLIENTE.
 */
public record ClienteRequestDTO(
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
}
