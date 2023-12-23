package fr.quiniou.gestion_back.appartement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.quiniou.gestion_back.contact.Contact;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Appartement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "proprietaire_id")
	@JsonBackReference
	private Utilisateur proprietaire;

	@OneToMany(mappedBy = "appartement", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private List<Contact> contacts;

	@ManyToMany
	@JoinTable(name = "appartement_gestionnaires", joinColumns = @JoinColumn(name = "appartement_id"), inverseJoinColumns = @JoinColumn(name = "gestionnaire_id"))
	private List<Utilisateur> gestionnaires;
	
    @Embedded
    private Adresse adresse;
    
	private Boolean publicAppartement;
}
