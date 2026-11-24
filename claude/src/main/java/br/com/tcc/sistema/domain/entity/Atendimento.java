package br.com.tcc.sistema.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade Atendimento - migrada da tabela ATENDIMENTO do sistema legado Unify NXJ.
 *
 * Campos preservados integralmente conforme atendimento.sql:
 *   ID, CLIENTE_ID, DATA_ABERTURA, ASSUNTO, DESCRICAO, SITUACAO
 *
 * SITUACAO: 'A' = Aberto, 'E' = Encerrado
 *
 * Regra de negócio do legado (CadastroAtendimento.fj): só é permitido criar/atualizar
 * atendimento vinculado a um cliente com SITUACAO = 'A' (ativo). Aplicada em AtendimentoService.
 */
@Entity
@Table(name = "ATENDIMENTO")
@SequenceGenerator(name = "seq_atendimento", sequenceName = "SEQ_ATENDIMENTO", allocationSize = 1)
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_atendimento")
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "DATA_ABERTURA", nullable = false)
    private LocalDate dataAbertura;

    @Column(name = "ASSUNTO", nullable = false, length = 60)
    private String assunto;

    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    private String situacao;

    public Atendimento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
