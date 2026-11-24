package br.com.tcc.sistema.domain.entity;

import jakarta.persistence.*;

/**
 * Entidade Cliente - migrada da tabela CLIENTE do sistema legado Unify NXJ.
 *
 * Campos preservados integralmente conforme cliente.sql:
 *   ID, NOME, TIPO_PESSOA, CPF_CNPJ, SITUACAO, TELEFONE, EMAIL,
 *   CEP, ESTADO, CIDADE, BAIRRO, LOGRADOURO, NUMERO
 *
 * TIPO_PESSOA: 'F' = Física, 'J' = Jurídica
 * SITUACAO:    'A' = Ativo,  'I' = Inativo
 *
 * Nao ha colecao de atendimentos nem cascade: no legado a FK FK_ATENDIMENTO_CLIENTE
 * nao possui ON DELETE CASCADE, ou seja, o banco recusa excluir cliente com atendimentos.
 * Essa regra e preservada em ClienteService.excluir.
 */
@Entity
@Table(name = "CLIENTE")
@SequenceGenerator(name = "seq_cliente", sequenceName = "SEQ_CLIENTE", allocationSize = 1)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cliente")
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 60)
    private String nome;

    @Column(name = "TIPO_PESSOA", nullable = false, length = 1)
    private String tipoPessoa;

    @Column(name = "CPF_CNPJ", nullable = false, length = 16)
    private String cpfCnpj;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    private String situacao;

    @Column(name = "TELEFONE", nullable = false, length = 16)
    private String telefone;

    @Column(name = "EMAIL", nullable = false, length = 48)
    private String email;

    @Column(name = "CEP", nullable = false, length = 11)
    private String cep;

    @Column(name = "ESTADO", nullable = false, length = 2)
    private String estado;

    @Column(name = "CIDADE", length = 30)
    private String cidade;

    @Column(name = "BAIRRO", length = 30)
    private String bairro;

    @Column(name = "LOGRADOURO", length = 58)
    private String logradouro;

    @Column(name = "NUMERO", length = 6)
    private String numero;

    public Cliente() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(String tipoPessoa) { this.tipoPessoa = tipoPessoa; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
}
