package fr.quiniou.gestion_back.appartement.dto;

import java.util.List;

import fr.quiniou.gestion_back.appartement.adresse.Adresse;
import fr.quiniou.gestion_back.appartement.details.AppartementDetails;
import fr.quiniou.gestion_back.appartement.statistiques.AppartementStatistiques;
import lombok.Data;

@Data
public class AppartementDTOForAdmin {
    private Long id;
	private Long proprietaireId;
	private Adresse adresse;
    private AppartementDetails details;
	private List<Long> contactIds;
	private Boolean publicAppartement;
    private AppartementStatistiques statistiques;
    private List<Long> fraisFixeIds;
    private List<Long> periodeLocationIds;
	private List<Long> gestionnaireIds;

}
