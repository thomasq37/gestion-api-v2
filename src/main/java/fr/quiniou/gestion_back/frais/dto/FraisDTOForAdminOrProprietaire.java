package fr.quiniou.gestion_back.frais.dto;

import lombok.Data;

@Data
public class FraisDTOForAdminOrProprietaire {
	private Long id;
	private String type;
	private Integer montant;
	private Integer frequence;
	private String debiteur;
	private Long periodeLocationId;
	private Long appartementId;
}
