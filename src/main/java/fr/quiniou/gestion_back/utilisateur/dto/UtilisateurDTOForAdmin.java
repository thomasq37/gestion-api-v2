package fr.quiniou.gestion_back.utilisateur.dto;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class UtilisateurDTOForAdmin {
	private Long id;
	private String nom;
    private String email;
    private String telNumero;
    private List<Long> appartementIds;
    private Set<Long> roleIds;

}
