package fr.quiniou.gestion_back.role;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoleService {
	
	private final RoleRepository roleRepository;
	
	@Autowired
	private RolePermissionsService customPermissionsService;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    // Obtenir tous les roles avec pagination
    public Page<Role> obtenirTousLesRoles(Pageable pageable) {
    	if (!customPermissionsService.peutGererRoles()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
        return roleRepository.findAll(pageable);
    }

    // Obtenir un role par son ID
    public Optional<Role> obtenirRoleParId(Long id) {
    	if (!customPermissionsService.peutGererRoles()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
        return roleRepository.findById(id);
    }

    // Ajouter un role
    public Role ajouterRole(Role role) {
    	if (!customPermissionsService.peutGererRoles()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
        return roleRepository.save(role);
    }

    // Mettre à jour un role
    public Role mettreAJourRole(Long id, Role detailsRole) {
    	if (!customPermissionsService.peutGererRoles()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
        // Vérifier si le role existe, puis mettre à jour
    	Role role = roleRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Role avec ID " + id + " non trouvé"));

        // Mettre à jour les attributs du role
        role.setNom(detailsRole.getNom());
        
        return roleRepository.save(role);
    }

    // Supprimer un role
    public void supprimerRole(Long id) {
    	if (!customPermissionsService.peutGererRoles()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
        // Vérifier si le role existe, puis le supprimer
        boolean exists = roleRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Role avec ID " + id + " non trouvé");
        }

        roleRepository.deleteById(id);
    }

	
	

}
