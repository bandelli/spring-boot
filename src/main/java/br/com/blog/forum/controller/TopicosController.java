package br.com.blog.forum.controller;

import br.com.blog.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.blog.forum.controller.dto.TopicoDto;
import br.com.blog.forum.controller.form.AtualizacaoTopicoForm;
import br.com.blog.forum.controller.form.TopicoForm;
import br.com.blog.forum.modelo.Topico;
import br.com.blog.forum.repository.CursoRepository;
import br.com.blog.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;
	@Autowired
	private CursoRepository cursoRepository;

	@GetMapping
	public Page<TopicoDto> lista(@RequestParam (required = false) String nomeCurso,
								 // Pode configurar assim	ou	 @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
								 Pageable paginacao) {

		// assim Pageable paginacao = PageRequest.of(pagina, qtd, Sort.Direction.ASC, ordenacao);

		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}
	}

	@PostMapping
	@Transactional
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder){
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);

		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){
		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()){
			return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
		}

		return ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form ){
		Optional<Topico> option = topicoRepository.findById(id);
		if (option.isPresent()){
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> remover(@PathVariable Long id){
		Optional<Topico> option = topicoRepository.findById(id);
		if (option.isPresent()){
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}
