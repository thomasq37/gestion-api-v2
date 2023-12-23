package fr.quiniou.gestion_back.utilisateur;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{
	// étends des méhodes de JpaRepository
	Optional<Utilisateur> findByNom(String nom);
    boolean existsByNom(String nom);
    Page<Utilisateur> findByIdIn(List<Long> ids, Pageable pageable);
}
