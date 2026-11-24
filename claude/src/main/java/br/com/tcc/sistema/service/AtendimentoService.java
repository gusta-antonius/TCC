package br.com.tcc.sistema.service;

import static br.com.tcc.sistema.service.CampoUtil.nuloSeVazio;
import static br.com.tcc.sistema.service.CampoUtil.texto;
import static br.com.tcc.sistema.service.CampoUtil.verificar;

import br.com.tcc.sistema.domain.entity.Atendimento;
import br.com.tcc.sistema.domain.validation.TextoUtil;
import br.com.tcc.sistema.domain.validation.ValidadorClienteAtendimento;
import br.com.tcc.sistema.domain.validation.ValidadorDataAbertura;
import br.com.tcc.sistema.dto.AtendimentoRequestDTO;
import br.com.tcc.sistema.dto.AtendimentoResponseDTO;
import br.com.tcc.sistema.exception.RecursoNaoEncontradoException;
import br.com.tcc.sistema.exception.ValidacaoNegocioException;
import br.com.tcc.sistema.repository.AtendimentoRepository;
import br.com.tcc.sistema.repository.ClienteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cadastro de atendimentos (migração de forms/CadastroAtendimento.fj).
 *
 * validarGravacao reproduz, na mesma ordem, o método validarGravacao() do formulário legado
 * (BEFORE ADD / BEFORE UPDATE), inclusive a conferência da situação do cliente, que também
 * ocorre na atualização.
 */
@Service
public class AtendimentoService {

    private static final int MAX_ASSUNTO = 60;
    private static final int MAX_DESCRICAO = 200;
    /** Status de falha genérica na consulta (qualquer valor diferente de 0 e -35 no NXJ). */
    private static final int STATUS_FALHA = -1;

    private final AtendimentoRepository atendimentoRepository;
    private final ClienteRepository clienteRepository;

    public AtendimentoService(AtendimentoRepository atendimentoRepository, ClienteRepository clienteRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponseDTO> listar() {
        return atendimentoRepository.findAll(Sort.by("id")).stream().map(AtendimentoResponseDTO::de).toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoResponseDTO buscar(Long id) {
        return AtendimentoResponseDTO.de(obter(id));
    }

    @Transactional
    public AtendimentoResponseDTO criar(AtendimentoRequestDTO dto) {
        Long clienteId = validarGravacao(dto);
        Atendimento atendimento = new Atendimento();
        preencher(atendimento, clienteId, dto);
        return AtendimentoResponseDTO.de(atendimentoRepository.save(atendimento));
    }

    @Transactional
    public AtendimentoResponseDTO atualizar(Long id, AtendimentoRequestDTO dto) {
        Atendimento atendimento = obter(id);
        Long clienteId = validarGravacao(dto);
        preencher(atendimento, clienteId, dto);
        return AtendimentoResponseDTO.de(atendimentoRepository.save(atendimento));
    }

    /** O legado não possui validação em BEFORE DELETE; a exclusão é direta. */
    @Transactional
    public void excluir(Long id) {
        atendimentoRepository.delete(obter(id));
    }

    private Atendimento obter(Long id) {
        return atendimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento não encontrado."));
    }

    private void preencher(Atendimento a, Long clienteId, AtendimentoRequestDTO dto) {
        a.setCliente(clienteRepository.getReferenceById(clienteId));
        a.setDataAbertura(LocalDate.parse(texto(dto.dataAbertura()), AtendimentoResponseDTO.FORMATO_DATA));
        a.setAssunto(texto(dto.assunto()));
        a.setDescricao(nuloSeVazio(dto.descricao()));
        a.setSituacao(texto(dto.situacao()));
    }

    /** Mesma sequência de CadastroAtendimento.fj → validarGravacao(). Retorna o id do cliente validado. */
    private Long validarGravacao(AtendimentoRequestDTO dto) {
        Long clienteId = validarCliente(dto.clienteId());

        verificar("dataAbertura", ValidadorDataAbertura.validar(texto(dto.dataAbertura())));

        String situacao = texto(dto.situacao()).toUpperCase();
        if (situacao.isEmpty()) {
            throw new ValidacaoNegocioException("situacao", "Informe a situação: A (aberto) ou E (encerrado).");
        }
        if (!"A".equals(situacao) && !"E".equals(situacao)) {
            throw new ValidacaoNegocioException("situacao", "Situação inválida. Use A (aberto) ou E (encerrado).");
        }

        String assunto = texto(dto.assunto());
        if (assunto.isEmpty()) {
            throw new ValidacaoNegocioException("assunto", "Informe o assunto.");
        }
        if (assunto.length() > MAX_ASSUNTO) {
            throw new ValidacaoNegocioException("assunto", "Assunto deve ter no máximo 60 caracteres.");
        }
        if (texto(dto.descricao()).length() > MAX_DESCRICAO) {
            throw new ValidacaoNegocioException("descricao", "Descrição deve ter no máximo 200 caracteres.");
        }
        return clienteId;
    }

    /**
     * Equivale a clienteInvalido() do legado: valida o formato do id e consulta a situação do
     * cliente ("select situacao from cliente where id = :cliente_id"); só cliente ativo é aceito.
     */
    private Long validarCliente(String clienteIdInformado) {
        String texto = texto(clienteIdInformado);
        verificar("clienteId", ValidadorClienteAtendimento.validarId(texto));
        Long clienteId = Long.valueOf(TextoUtil.apenasDigitos(texto));

        int status;
        String situacao = null;
        try {
            Optional<String> consulta = clienteRepository.buscarSituacaoPorId(clienteId);
            if (consulta.isPresent()) {
                status = ValidadorClienteAtendimento.STATUS_SUCESSO;
                situacao = consulta.get();
            } else {
                status = ValidadorClienteAtendimento.STATUS_NAO_ENCONTRADO;
            }
        } catch (DataAccessException e) {
            status = STATUS_FALHA;
        }
        verificar("clienteId", ValidadorClienteAtendimento.validarConsulta(status, situacao));
        return clienteId;
    }
}
