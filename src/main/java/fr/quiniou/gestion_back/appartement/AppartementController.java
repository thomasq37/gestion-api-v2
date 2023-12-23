package fr.quiniou.gestion_back.appartement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/appartements", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class AppartementController {

    private final AppartementService appartementService;
    
    
    @Autowired
    public AppartementController(AppartementService appartementService) {
        this.appartementService = appartementService;
    }
    
 // Obtenir tous les appartements avec DTO
    @GetMapping
    public ResponseEntity<Page<AppartementDTO>> obtenirTousLesAppartements(Pageable pageable) {
        Page<AppartementDTO> appartements = appartementService.obtenirTousLesAppartements(pageable);
        return new ResponseEntity<>(appartements, HttpStatus.OK);
    }

    // Obtenir un appartement par son ID avec DTO
    @GetMapping("/{id}")
    public ResponseEntity<AppartementDTO> obtenirAppartementParId(@PathVariable Long id) {
        AppartementDTO appartement = appartementService.obtenirAppartementParId(id);
        return new ResponseEntity<>(appartement, HttpStatus.OK);
    }

    // Ajouter un appartement avec DTO
    @PostMapping
    public ResponseEntity<Appartement> ajouterAppartement(@RequestBody Appartement appartement) {
        Appartement appartementEnregistre = appartementService.ajouterAppartement(appartement);
        return new ResponseEntity<>(appartementEnregistre, HttpStatus.CREATED);
    }

    // Supprimer un appartement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAppartement(@PathVariable Long id) {
        appartementService.supprimerAppartement(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    
    // ------------------------------- Section Adressse Appartement ------------------------------- //
    
    
    // Obtenir adresse appartement par id
    @GetMapping("/{id}/adresse")
    public ResponseEntity<Adresse> obtenirAdresseAppartementParAppartementId(@PathVariable Long id) {
    	try {
            Adresse adresse = appartementService.obtenirAdresseAppartementParAppartementId(id);
            return ResponseEntity.ok(adresse);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    
	// Mettre a jour adresse appartement par id
    @PutMapping("/{id}/adresse")
    public ResponseEntity<Adresse> majAdresseAppartementParAppartementId(@PathVariable Long id, @RequestBody Adresse nouvelleAdresse) {
        try {
            Adresse adresse = appartementService.majAdresseAppartementParAppartementId(id, nouvelleAdresse);
            return ResponseEntity.ok(adresse);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    // Supprimer (ou effacer) l'adresse d'un appartement
    @DeleteMapping("/{id}/adresse")
    public ResponseEntity<?> supprimerAdresseParAppartementId(@PathVariable Long id) {
        try {
            appartementService.supprimerAdresseParAppartementId(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
