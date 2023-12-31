package fr.quiniou.gestion_back.utilisateur;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.appartement.AppartementRepository;
import fr.quiniou.gestion_back.auth.PermissionsByRolesService;
import fr.quiniou.gestion_back.contact.ContactRepository;
import fr.quiniou.gestion_back.frais.FraisRepository;
import fr.quiniou.gestion_back.role.Role;
import fr.quiniou.gestion_back.utilisateur.dto.GestionnaireDTO;
import fr.quiniou.gestion_back.utilisateur.dto.UtilisateurDTOForAdmin;
import fr.quiniou.gestion_back.utilisateur.dto.UtilisateurDTOForPublicAppartement;

@Service
public class UtilisateurPermissionsService extends PermissionsByRolesService  {

	public UtilisateurPermissionsService(
			AppartementRepository appartementRepository,
			UtilisateurRepository utilisateurRepository, 
			ContactRepository contactRepository, 
			FraisRepository fraisRepository) {
		super(appartementRepository, utilisateurRepository, contactRepository, fraisRepository);
	}
	
    public Page<Object> obtenirUtilisateursAutorises(Pageable pageable) {
        Utilisateur currentUser = getCurrentUser();
        if (isUserInRole(ROLE_ADMIN)) {
            // Convertir tous les utilisateurs en DTOs
            return utilisateurRepository.findAll(pageable)
                .map(this::convertToUtilisateurDTOForAdmin);
        } 
        else {
        	
        	List<Object> utilisateursDtos = new ArrayList<>();
            // Si role proprietaire
			if (isUserInRole(ROLE_PROPRIETAIRE)) {
                List<Long> gestionnaireIds = currentUser.getAppartements().stream()
                    .flatMap(appartement -> appartement.getGestionnaires().stream())
                    .map(Utilisateur::getId)
                    .distinct()
                    .collect(Collectors.toList());
                // Ajouter le propriétaire en tant que DTO
                utilisateursDtos = utilisateurRepository.findByIdIn(gestionnaireIds, pageable)
                    .stream()
                    .map(this::convertToGestionnaireDTO)
                    .collect(Collectors.toList());

                // Ajouter le propriétaire en tant que DTO
                utilisateursDtos.add(convertToUtilisateurDTOForAdmin(currentUser));

            } 
			// Sinon on ajouter seulement le compte connecté
			else {
            	utilisateursDtos.add(convertToUtilisateurDTOForAdmin(currentUser));
            }
			// Ajouter les appartements avec publicAppartement à true
            List<Appartement> publicAppartements = appartementRepository.findByPublicAppartementTrue();
            Set<Long> addedUserIds = new HashSet<>();
            for (Appartement appartement : publicAppartements) {
                Utilisateur utilisateur = appartement.getProprietaire();
            	if (addedUserIds.add(utilisateur.getId())) { // Retourne false si l'élément a deja été ajouté
            		utilisateursDtos.add(convertToUtilisateurDTOForPublicAppartement(utilisateur));
                }
            }
            return new PageImpl<>(utilisateursDtos, pageable, utilisateursDtos.size());

        }
    }

    public boolean peutGererUtilisateur(Long idUtilisateurCible, String operation) {
        Utilisateur currentUser = getCurrentUser();
        // Les administrateurs peuvent effectuer n'importe quelle opération
        if (isUserInRole(ROLE_ADMIN)) {
            return true;
        }

        // Pour les autres rôles, vérifiez si l'opération est autorisée sur leur propre compte
        if (idUtilisateurCible.equals(currentUser.getId()) &&
            (operation.equals(ACT_OBTENIR) || operation.equals(ACT_MAJ) || operation.equals(ACT_SUPPRIMER))) {
            return true;
        }

        // Un propriétaire peut gérer les utilisateurs gestionnaires de ses appartements
        if (operation.equals(ACT_OBTENIR) || operation.equals(ACT_MAJ) || operation.equals(ACT_SUPPRIMER)) {
            return currentUser.getAppartements().stream()
                .flatMap(appartement -> appartement.getGestionnaires().stream())
                .anyMatch(gestionnaire -> gestionnaire.getId().equals(idUtilisateurCible));
        }

        return false;
    }

    public UtilisateurDTOForAdmin convertToUtilisateurDTOForAdmin(Utilisateur utilisateur) {
        UtilisateurDTOForAdmin dto = new UtilisateurDTOForAdmin();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelNumero(utilisateur.getTelNumero());

        // Convertir la liste d'appartements en liste d'identifiants d'appartement
        List<Long> appartementIds = utilisateur.getAppartements().stream()
                                        .map(Appartement::getId)
                                        .collect(Collectors.toList());
        dto.setAppartementIds(appartementIds);

        // Convertir le Set de rôles en Set d'identifiants de rôle
        Set<Long> roleIds = utilisateur.getRoles().stream()
                                .map(Role::getId)
                                .collect(Collectors.toSet());
        dto.setRoleIds(roleIds);

        return dto;
    }
    
    public GestionnaireDTO convertToGestionnaireDTO(Utilisateur utilisateur) {
    	GestionnaireDTO dto = new GestionnaireDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelNumero(utilisateur.getTelNumero());

        // Convertir le Set de rôles en Set d'identifiants de rôle
        Set<Long> roleIds = utilisateur.getRoles().stream()
                                .map(Role::getId)
                                .collect(Collectors.toSet());
        dto.setRoleIds(roleIds);

        return dto;
    }

    public UtilisateurDTOForPublicAppartement convertToUtilisateurDTOForPublicAppartement(Utilisateur utilisateur) {
        UtilisateurDTOForPublicAppartement dto = new UtilisateurDTOForPublicAppartement();
        
        dto.setNom(utilisateur.getNom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelNumero(utilisateur.getTelNumero());
        
        // Filtrer et ajouter les IDs des appartements publics
        List<Long> publicAppartementIds = utilisateur.getAppartements().stream()
            .filter(Appartement::getPublicAppartement)
            .map(Appartement::getId)
            .collect(Collectors.toList());

        dto.setPublicAppartementIds(publicAppartementIds);

        return dto;
    }
    


}
