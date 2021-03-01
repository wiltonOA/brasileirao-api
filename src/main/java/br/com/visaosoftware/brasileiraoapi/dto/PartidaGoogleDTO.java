package br.com.visaosoftware.brasileiraoapi.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // Construtor vazio
@AllArgsConstructor // Construtor com todos os atributos
public class PartidaGoogleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String statusPartida;
	private String tempoPartida;
	// Dados equipe casa
	private String nomeEquipeCasa;
	private String urlLogoEquipeCasa;
	private String placarEquipeCasa;
	private String golsEquipeCasa;
	private String placarEstendidoEquipeCasa;
	// Dados equipe Visitante
	private String nomeEquipeVisitante;
	private String urlLogoEquipeVisitante;
	private String placarEquipeVisitante;
	private String golsEquipeVisitante;
	private String placarEstendidoEquipeVisitante;
	
	

}
