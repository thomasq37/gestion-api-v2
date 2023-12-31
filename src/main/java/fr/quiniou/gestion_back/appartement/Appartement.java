package fr.quiniou.gestion_back.appartement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.quiniou.gestion_back.appartement.adresse.Adresse;
import fr.quiniou.gestion_back.appartement.details.AppartementDetails;
import fr.quiniou.gestion_back.appartement.statistiques.AppartementStatistiques;
import fr.quiniou.gestion_back.contact.Contact;
import fr.quiniou.gestion_back.frais.Frais;
import fr.quiniou.gestion_back.periode_location.PeriodeLocation;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
	private Utilisateur proprietaire;

	@OneToMany(mappedBy = "appartement", cascade = CascadeType.REMOVE)
	private List<Contact> contacts;

	@ManyToMany
	@JoinTable(name = "appartement_gestionnaires", joinColumns = @JoinColumn(name = "appartement_id"), inverseJoinColumns = @JoinColumn(name = "gestionnaire_id"))
	private List<Utilisateur> gestionnaires;
	
    @Embedded
    private Adresse adresse;
    
    @Embedded
    private AppartementDetails details;
    
    @Column(nullable=true)
	private Boolean publicAppartement;
    
    @Embedded
    private AppartementStatistiques statistiques;
    
    @OneToMany(mappedBy = "appartement", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Frais> fraisFixes;
    
    @OneToMany(mappedBy = "appartement", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PeriodeLocation> periodesLocations;
}
