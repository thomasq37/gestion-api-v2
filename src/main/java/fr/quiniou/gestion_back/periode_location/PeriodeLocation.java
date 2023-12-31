package fr.quiniou.gestion_back.periode_location;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.frais.Frais;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class PeriodeLocation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartement_id")
    private Appartement appartement;
    
    @Column(name = "prix_location")
    private Double prixLocation;

    @Column(name = "is_loc_vac")
    private Boolean isLocVac;
    
    @Column(name = "est_entree")
    private LocalDate estEntree;

    @Column(name = "est_sortie")
    private LocalDate estSortie;

	@OneToMany(mappedBy = "periodeLocation", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Frais> frais;

}
