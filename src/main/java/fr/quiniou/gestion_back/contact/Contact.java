package fr.quiniou.gestion_back.contact;

import com.fasterxml.jackson.annotation.JsonBackReference;

import fr.quiniou.gestion_back.appartement.Appartement;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nom;
	private String email;
	private String telNumero;

	@ManyToOne
	@JoinColumn(name = "appartement_id")
	@JsonBackReference
	private Appartement appartement;
}
