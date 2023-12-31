package fr.quiniou.gestion_back.frais;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.periode_location.PeriodeLocation;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Frais {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String type;
	private Integer montant;
	private Integer frequence;
	private String debiteur;

	@ManyToOne
	@JoinColumn(name = "periode_location_id", nullable = true)
	private PeriodeLocation periodeLocation;

	@ManyToOne
	@JoinColumn(name = "appartement_id", nullable = true)
	private Appartement appartement;
}
