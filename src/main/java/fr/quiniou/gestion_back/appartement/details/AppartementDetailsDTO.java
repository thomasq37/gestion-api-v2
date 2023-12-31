package fr.quiniou.gestion_back.appartement.details;

import lombok.Data;

@Data
public class AppartementDetailsDTO {
	private Integer nombrePieces;
	private Double surface;
	private Boolean balcon;
	private String dpe;
}
