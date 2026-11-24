package br.com.tcc.sistema.repository;

import br.com.tcc.sistema.domain.entity.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    /** Usado para reproduzir a restrição da FK FK_ATENDIMENTO_CLIENTE ao excluir um cliente. */
    boolean existsByClienteId(Long clienteId);
}
