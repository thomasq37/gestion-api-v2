package fr.quiniou.gestion_back.frais;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.quiniou.gestion_back.utilisateur.Utilisateur;

@Repository
public interface FraisRepository extends JpaRepository<Frais, Long> {

	Page<Frais> findAllByAppartementIdIn(List<Long> appartementIds, Pageable pageable);

	Page<Frais> findAllByPeriodeLocationAppartementGestionnaires(Utilisateur currentUser, Pageable pageable);

}
