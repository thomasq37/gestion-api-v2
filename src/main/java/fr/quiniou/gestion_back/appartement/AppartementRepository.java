package fr.quiniou.gestion_back.appartement;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.quiniou.gestion_back.utilisateur.Utilisateur;

@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Long> {
	    
    // Retourne tous les appartements pour un utilisateur donné (propriétaire).
    Page<Appartement> findByProprietaire(Utilisateur utilisateur, Pageable pageable);

    // Retourne tous les appartements où un utilisateur donné (gestionnaire) est listé.
    Page<Appartement> findByGestionnairesContains(Utilisateur gestionnaire, Pageable pageable);

    // Retourne tous les appartements marqués comme publics.
    Page<Appartement> findByPublicAppartementIsTrue(Pageable pageable);

	List<Appartement> findAllByProprietaireNom(String currentUserName);
	
    List<Appartement> findByPublicAppartementTrue();
}