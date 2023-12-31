package fr.quiniou.gestion_back.periode_location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/periodes-locations", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class PeriodeLocationController {

    private final PeriodeLocationService periodeLocationService;

    public PeriodeLocationController(PeriodeLocationService periodeLocationService) {
        this.periodeLocationService = periodeLocationService;
    }
    
    // Obtenir toutes les periodes de locations avec pagination
    @GetMapping
    public ResponseEntity<Object> obtenirTousLesPeriodesLocations(Pageable pageable) {
        try {
        	Page<Object> periodesLocations = periodeLocationService.obtenirTousLesPeriodesLocations(pageable);
            return new ResponseEntity<>(periodesLocations, HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

 // Obtenir une periodeLocation par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenirPeriodeLocationParId(@PathVariable Long id) {
        try {
            Object periodeLocation = periodeLocationService.obtenirPeriodeLocationParId(id);
            return new ResponseEntity<>(periodeLocation, HttpStatus.OK);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Ajouter une periode le location
    @PostMapping
    public ResponseEntity<Object> ajouterPeriodeLocation(@RequestBody PeriodeLocation periodeLocation) {
    	try {
    		PeriodeLocationDTO periodeLocationEnregistre = periodeLocationService.ajouterPeriodeLocation(periodeLocation);
            return new ResponseEntity<>(periodeLocationEnregistre, HttpStatus.CREATED);

    	}
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Mettre à jour une periode de location
    @PatchMapping("/{id}")
    public ResponseEntity<Object> mettreAJourPeriodeLocation(@PathVariable Long id, @RequestBody PeriodeLocationDTO detailsPeriodeLocation) {
    	try {
    		Object periodeLocationMiseAJour = periodeLocationService.mettreAJourPeriodeLocation(id, detailsPeriodeLocation);
            return new ResponseEntity<>(periodeLocationMiseAJour, HttpStatus.OK);
    	}
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

 // Supprimer une période de location
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> supprimerPeriodeLocation(@PathVariable Long id) {
        try {
        	periodeLocationService.supprimerPeriodeLocation(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    
}
