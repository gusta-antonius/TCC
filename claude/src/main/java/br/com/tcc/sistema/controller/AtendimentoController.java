package br.com.tcc.sistema.controller;

import br.com.tcc.sistema.dto.AtendimentoRequestDTO;
import br.com.tcc.sistema.dto.AtendimentoResponseDTO;
import br.com.tcc.sistema.service.AtendimentoService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Operações do formulário CadastroAtendimento: incluir, consultar, alterar e excluir. */
@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentoController {

    private final AtendimentoService service;

    public AtendimentoController(AtendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AtendimentoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AtendimentoResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> criar(@RequestBody AtendimentoRequestDTO dto) {
        AtendimentoResponseDTO criado = service.criar(dto);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public AtendimentoResponseDTO atualizar(@PathVariable Long id, @RequestBody AtendimentoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
