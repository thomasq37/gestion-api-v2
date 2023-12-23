package fr.quiniou.gestion_back.utilisateur;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.role.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Utilisateur {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nom;
    private String mdp;  
    
    @Column(nullable = true)
    private String email;
    
    @Column(nullable = true)
    private String telNumero;
    
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.REMOVE)
	@JsonManagedReference
    private List<Appartement> appartements;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles;
}
