package com.empreget.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.empreget.api.assembler.PrestadorAssembler;
import com.empreget.api.assembler.PrestadorInputDisassembler;
import com.empreget.api.dto.PrestadorResponse;
import com.empreget.api.dto.input.PrestadorInput;
import com.empreget.domain.exception.NegocioException;
import com.empreget.domain.exception.PrestadorNaoEncontradoException;
import com.empreget.domain.model.Prestador;
import com.empreget.domain.repository.PrestadorRepository;
import com.empreget.domain.service.catalogoPrestadorService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/prestadores")
public class PrestadorController {

	private PrestadorRepository prestadorRepository;
	private catalogoPrestadorService catalogoPrestadorService;
	private PrestadorAssembler assembler;
	private PrestadorInputDisassembler prestadorInputDisassembler;

	@GetMapping
	public List<PrestadorResponse> listar(){
		return prestadorRepository.findAll()
				.stream()
				.map(prestador -> assembler.toModel(prestador))
				.collect(Collectors.toList());
	}
	
	@GetMapping("/{prestadorId}")
	public Prestador buscar(@PathVariable Long prestadorId){
		return catalogoPrestadorService.buscarOuFalhar(prestadorId);
		
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PrestadorResponse adicionar(@Valid @RequestBody PrestadorInput prestadorInput) {
		
		Prestador prestador = prestadorInputDisassembler.toDomainObject(prestadorInput);
		return assembler.toModel(catalogoPrestadorService.salvar(prestador));

	}
	
	@PutMapping("/{prestadorId}")
	public PrestadorResponse editar(@PathVariable Long prestadorId, @RequestBody @Valid PrestadorInput prestadorInput) {
		Prestador prestador = prestadorInputDisassembler.toDomainObject(prestadorInput);
		
		Prestador prestadorAtual = catalogoPrestadorService.buscarOuFalhar(prestadorId);
		
		BeanUtils.copyProperties(prestador, prestadorAtual, 
				"id", "dataDoCadastro", "dataDaAtualizacao");
		
		return assembler.toModel(catalogoPrestadorService.salvar(prestadorAtual));
	}
	
//	@PatchMapping("/{prestadorId}")
//	public Prestador editarParcial(@PathVariable Long prestadorId, @Valid @RequestBody Map<String, Object> dados) {
//		
//		Prestador prestadorAtual = catalogoPrestadorService.buscarOuFalhar(prestadorId);
//		
//		merge(dados, prestadorAtual);
//		
//		return editar(prestadorId, prestadorAtual);
//	}
//	
	private void merge(Map<String, Object> dadosOrigem, Prestador prestadorDestino) {
		ObjectMapper objectMapper = new ObjectMapper();
		Prestador prestadorOrigem = objectMapper.convertValue(dadosOrigem, Prestador.class);
		
		dadosOrigem.forEach((nomePropriedade, valorPropriedade) -> {
			Field field = ReflectionUtils.findField(Prestador.class, nomePropriedade);
			field.setAccessible(true);
			
			Object novoValor = ReflectionUtils.getField(field, prestadorOrigem);
			
			ReflectionUtils.setField(field, prestadorDestino, novoValor);
		});
	}

	@DeleteMapping("/{prestadorId}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void excluir (@PathVariable Long prestadorId){		
		catalogoPrestadorService.excluir(prestadorId);
		
	}
	
	
}
