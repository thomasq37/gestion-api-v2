package fr.quiniou.gestion_back.periode_location;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PeriodeLocationRepository extends JpaRepository<PeriodeLocation, Long> {

	Page<PeriodeLocation> findAllByAppartementIdIn(List<Long> appartementIds, Pageable pageable);

	@Query("SELECT pl FROM PeriodeLocation pl JOIN pl.appartement a JOIN a.gestionnaires g WHERE g.id = :gestionnaireId")
	Page<PeriodeLocation> findAllByPeriodeLocationAppartementGestionnaires(Long gestionnaireId, Pageable pageable);
}
