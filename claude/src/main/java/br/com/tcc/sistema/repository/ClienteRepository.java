package br.com.tcc.sistema.repository;

import br.com.tcc.sistema.domain.entity.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Equivale ao "select situacao from cliente where id = :cliente_id" do CadastroAtendimento.fj. */
    @Query("select c.situacao from Cliente c where c.id = :id")
    Optional<String> buscarSituacaoPorId(@Param("id") Long id);
}
