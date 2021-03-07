package br.com.visaosoftware.brasileiraoapi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "partida")
public class Partida implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "partida_id")
	private Long id;
	
	@Transient
	private String statusPartida;
	
	@ManyToOne
	@Column(name = "equipe_casa_id")
	private Equipe equipeCasa;
	
	@ManyToOne
	@Column(name = "equipe_visitante_id")
	private Equipe equipeVisitante;
	
	@Column(name = "placar_equipe_casa")
	private Integer placraEquipeCasa;
	
	@Column(name = "placar_equipe_visitante")
	private Integer placarEqipeVisitante;
	
	@Column(name = "gols_equipe_casa")
	private String golsEqipeCasa;
	
	@Column(name = "gols_equipe_visitante")
	private String golsEquipeVisitante;
	
	@Column(name = "placar_estendido_equipe_casa")
	private Integer placarEstendidoEquipeCasa;
	
	@Column(name = "placar_estendido_equipe_visitante")
	private Integer placarEstendidoEquipeVisitante;
	
	@ApiModelProperty(example = "dd/MM/yyyy HH:mm")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "America/Sao_Paulo")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_hora_jogo")
	private Date dataHoraPartida;
	
	@Column(name = "local_partida")
	private String localPartica;
	
}
