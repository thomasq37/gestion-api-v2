package fr.quiniou.gestion_back.utilisateur.dto;

import java.util.List;

import lombok.Data;

@Data
public class UtilisateurDTOForPublicAppartement {
	private String nom;
    private String email;
    private String telNumero;
    private List<Long> publicAppartementIds;

}
