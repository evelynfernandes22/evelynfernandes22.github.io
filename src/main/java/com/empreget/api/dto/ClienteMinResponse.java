package com.empreget.api.dto;

import lombok.Data;

@Data
public class ClienteMinResponse {
	
	private Long id;
	private String nome;
	EnderecoResponse endereco;
	private String telefone;

}