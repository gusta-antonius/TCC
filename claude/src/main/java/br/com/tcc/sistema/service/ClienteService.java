package br.com.tcc.sistema.service;

import static br.com.tcc.sistema.service.CampoUtil.nuloSeVazio;
import static br.com.tcc.sistema.service.CampoUtil.texto;
import static br.com.tcc.sistema.service.CampoUtil.verificar;
import static br.com.tcc.sistema.service.CampoUtil.verificarTamanho;

import br.com.tcc.sistema.domain.entity.Cliente;
import br.com.tcc.sistema.domain.validation.ValidadorCep;
import br.com.tcc.sistema.domain.validation.ValidadorDocumento;
import br.com.tcc.sistema.domain.validation.ValidadorEmail;
import br.com.tcc.sistema.domain.validation.ValidadorNome;
import br.com.tcc.sistema.domain.validation.ValidadorTelefone;
import br.com.tcc.sistema.domain.validation.ValidadorUf;
import br.com.tcc.sistema.dto.ClienteRequestDTO;
import br.com.tcc.sistema.dto.ClienteResponseDTO;
import br.com.tcc.sistema.exception.ConflitoNegocioException;
import br.com.tcc.sistema.exception.RecursoNaoEncontradoException;
import br.com.tcc.sistema.exception.ValidacaoNegocioException;
import br.com.tcc.sistema.repository.AtendimentoRepository;
import br.com.tcc.sistema.repository.ClienteRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cadastro de clientes (migração de forms/CadastroCliente.fj).
 *
 * validarGravacao reproduz, na mesma ordem, o método validarGravacao() do formulário legado,
 * executado em BEFORE ADD e BEFORE UPDATE.
 */
@Service
public class ClienteService {

    // Tamanhos das colunas de CLIENTE (legado/scripts/cliente.sql). No legado o limite era imposto
    // pelo field_length do campo de tela; na API ele precisa ser verificado explicitamente.
    private static final int MAX_DOCUMENTO = 16;
    private static final int MAX_TELEFONE = 16;
    private static final int MAX_CEP = 11;
    private static final int MAX_CIDADE = 30;
    private static final int MAX_BAIRRO = 30;
    private static final int MAX_LOGRADOURO = 58;
    private static final int MAX_NUMERO = 6;

    private final ClienteRepository clienteRepository;
    private final AtendimentoRepository atendimentoRepository;

    public ClienteService(ClienteRepository clienteRepository, AtendimentoRepository atendimentoRepository) {
        this.clienteRepository = clienteRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listar() {
        return clienteRepository.findAll(Sort.by("id")).stream().map(ClienteResponseDTO::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscar(Long id) {
        return ClienteResponseDTO.de(obter(id));
    }

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        validarGravacao(dto);
        Cliente cliente = new Cliente();
        preencher(cliente, dto);
        return ClienteResponseDTO.de(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = obter(id);
        validarGravacao(dto);
        preencher(cliente, dto);
        return ClienteResponseDTO.de(clienteRepository.save(cliente));
    }

    /**
     * No legado a FK FK_ATENDIMENTO_CLIENTE não tem ON DELETE CASCADE: o banco recusa excluir
     * um cliente que possui atendimentos. A mesma regra é aplicada aqui antes do DELETE.
     */
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = obter(id);
        if (atendimentoRepository.existsByClienteId(id)) {
            throw new ConflitoNegocioException(
                    "Não é possível excluir o cliente: existem atendimentos vinculados a ele.");
        }
        clienteRepository.delete(cliente);
    }

    private Cliente obter(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    private void preencher(Cliente c, ClienteRequestDTO dto) {
        c.setNome(texto(dto.nome()));
        c.setTipoPessoa(texto(dto.tipoPessoa()));
        c.setCpfCnpj(texto(dto.cpfCnpj()));
        c.setSituacao(texto(dto.situacao()));
        c.setTelefone(texto(dto.telefone()));
        c.setEmail(texto(dto.email()));
        c.setCep(texto(dto.cep()));
        c.setEstado(texto(dto.estado()));
        c.setCidade(nuloSeVazio(dto.cidade()));
        c.setBairro(nuloSeVazio(dto.bairro()));
        c.setLogradouro(nuloSeVazio(dto.logradouro()));
        c.setNumero(nuloSeVazio(dto.numero()));
    }

    /** Mesma sequência de CadastroCliente.fj → validarGravacao(). */
    private void validarGravacao(ClienteRequestDTO dto) {
        String tipo = texto(dto.tipoPessoa()).toUpperCase();
        if (tipo.isEmpty()) {
            throw new ValidacaoNegocioException("tipoPessoa", "Informe o tipo de pessoa: F (física) ou J (jurídica).");
        }
        if (!"F".equals(tipo) && !"J".equals(tipo)) {
            throw new ValidacaoNegocioException("tipoPessoa", "Tipo inválido. Use F (física) ou J (jurídica).");
        }
        verificar("nome", ValidadorNome.validar(texto(dto.nome()), tipo));
        verificar("cpfCnpj", ValidadorDocumento.validar(tipo, texto(dto.cpfCnpj())));

        String situacao = texto(dto.situacao()).toUpperCase();
        if (situacao.isEmpty()) {
            throw new ValidacaoNegocioException("situacao", "Informe a situação: A (ativo) ou I (inativo).");
        }
        if (!"A".equals(situacao) && !"I".equals(situacao)) {
            throw new ValidacaoNegocioException("situacao", "Situação inválida. Use A (ativo) ou I (inativo).");
        }

        String estado = texto(dto.estado());
        verificar("estado", ValidadorUf.validar(estado));
        verificar("cep", ValidadorCep.validar(texto(dto.cep())));
        verificar("cep", ValidadorCep.compativelComUf(texto(dto.cep()), estado));
        verificar("telefone", ValidadorTelefone.validar(texto(dto.telefone())));
        verificar("telefone", ValidadorTelefone.compativelComUf(texto(dto.telefone()), estado));
        verificar("email", ValidadorEmail.validar(texto(dto.email())));

        // Acréscimo da migração (ver README): limites de coluna antes impostos pela tela.
        verificarTamanho("cpfCnpj", "CPF/CNPJ", texto(dto.cpfCnpj()), MAX_DOCUMENTO);
        verificarTamanho("telefone", "Telefone", texto(dto.telefone()), MAX_TELEFONE);
        verificarTamanho("cep", "CEP", texto(dto.cep()), MAX_CEP);
        verificarTamanho("cidade", "Cidade", texto(dto.cidade()), MAX_CIDADE);
        verificarTamanho("bairro", "Bairro", texto(dto.bairro()), MAX_BAIRRO);
        verificarTamanho("logradouro", "Logradouro", texto(dto.logradouro()), MAX_LOGRADOURO);
        verificarTamanho("numero", "Número", texto(dto.numero()), MAX_NUMERO);
    }
}
