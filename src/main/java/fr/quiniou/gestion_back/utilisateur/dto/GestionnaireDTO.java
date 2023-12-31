package fr.quiniou.gestion_back.utilisateur.dto;

import java.util.Set;

import lombok.Data;

@Data
public class GestionnaireDTO {

	private Long id;
	private String nom;
    private String email;
    private String telNumero;
    private Set<Long> roleIds;
}
