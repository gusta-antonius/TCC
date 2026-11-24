package br.com.tcc.sistema.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.tcc.sistema.domain.entity.Atendimento;
import br.com.tcc.sistema.domain.entity.Cliente;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/** Mapeamento JPA x tabelas CLIENTE/ATENDIMENTO (H2 em memória, schema.sql real). */
@SpringBootTest
@Transactional
class RepositoryPersistenciaTest {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    AtendimentoRepository atendimentoRepository;

    private Cliente novoCliente(String situacao) {
        Cliente c = new Cliente();
        c.setNome("João da Silva");
        c.setTipoPessoa("F");
        c.setCpfCnpj("52998224725");
        c.setSituacao(situacao);
        c.setTelefone("47999998888");
        c.setEmail("joao@exemplo.com");
        c.setCep("89280000");
        c.setEstado("SC");
        return c;
    }

    private Atendimento novoAtendimento(Cliente cliente) {
        Atendimento a = new Atendimento();
        a.setCliente(cliente);
        a.setDataAbertura(LocalDate.of(2026, 11, 24));
        a.setAssunto("Primeiro contato");
        a.setSituacao("A");
        return a;
    }

    @Test
    void salvaClienteGerandoIdPelaSequence() {
        Cliente primeiro = clienteRepository.saveAndFlush(novoCliente("A"));
        Cliente segundo = clienteRepository.saveAndFlush(novoCliente("A"));

        assertThat(primeiro.getId()).isNotNull();
        assertThat(segundo.getId()).isEqualTo(primeiro.getId() + 1);
        assertThat(clienteRepository.findById(primeiro.getId())).isPresent();
    }

    @Test
    void camposOpcionaisPodemFicarNulos() {
        Cliente salvo = clienteRepository.saveAndFlush(novoCliente("A"));
        assertThat(salvo.getCidade()).isNull();
        assertThat(salvo.getNumero()).isNull();
    }

    @Test
    void consultaSituacaoDoCliente() {
        Cliente ativo = clienteRepository.saveAndFlush(novoCliente("A"));
        Cliente inativo = clienteRepository.saveAndFlush(novoCliente("I"));

        assertThat(clienteRepository.buscarSituacaoPorId(ativo.getId())).contains("A");
        assertThat(clienteRepository.buscarSituacaoPorId(inativo.getId())).contains("I");
        assertThat(clienteRepository.buscarSituacaoPorId(-1L)).isEmpty();
    }

    @Test
    void salvaAtendimentoVinculadoAoCliente() {
        Cliente cliente = clienteRepository.saveAndFlush(novoCliente("A"));
        Atendimento salvo = atendimentoRepository.saveAndFlush(novoAtendimento(cliente));

        assertThat(salvo.getId()).isNotNull();
        assertThat(atendimentoRepository.existsByClienteId(cliente.getId())).isTrue();
        assertThat(atendimentoRepository.existsByClienteId(-1L)).isFalse();
    }
}
