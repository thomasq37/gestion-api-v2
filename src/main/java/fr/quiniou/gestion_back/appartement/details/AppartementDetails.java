package fr.quiniou.gestion_back.appartement.details;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AppartementDetails {
	private Integer nombrePieces;
	private Double surface;
	private Boolean balcon;
}
