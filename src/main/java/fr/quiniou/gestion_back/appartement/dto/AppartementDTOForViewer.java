package fr.quiniou.gestion_back.appartement.dto;

import java.util.List;

import fr.quiniou.gestion_back.appartement.adresse.Adresse;
import fr.quiniou.gestion_back.appartement.details.AppartementDetails;
import lombok.Data;

@Data
public class AppartementDTOForViewer {
		private Long proprietaireId;
		private Adresse adresse;
	    private AppartementDetails details;
		private List<Long> contactIds;
}
