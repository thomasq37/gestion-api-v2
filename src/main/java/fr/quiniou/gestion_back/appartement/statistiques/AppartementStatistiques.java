package fr.quiniou.gestion_back.appartement.statistiques;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AppartementStatistiques {
	private Integer prixAchat;
	private Integer prixEstimation;
	private String dpe;
}
