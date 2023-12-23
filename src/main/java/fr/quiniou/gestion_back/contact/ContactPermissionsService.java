package fr.quiniou.gestion_back.contact;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.appartement.AppartementRepository;
import fr.quiniou.gestion_back.security.CustomUtilisateurDetails;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class ContactPermissionsService {
    private final ContactRepository contactRepository;
    private final AppartementRepository appartementRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public ContactPermissionsService(
    		ContactRepository contactRepository, 
    		AppartementRepository appartementRepository,
    		UtilisateurRepository utilisateurRepository) {
        this.contactRepository = contactRepository;
        this.appartementRepository = appartementRepository;
        this.utilisateurRepository = utilisateurRepository;
    }
    
    public boolean peutAjouterContact(Contact contact) {
        Utilisateur currentUser = getCurrentUser();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        // Les administrateurs peuvent ajouter des contacts à n'importe quel appartement
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return true;
        }

        // Les propriétaires ne peuvent ajouter des contacts qu'à leurs propres appartements
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))) {
        	System.out.println(contact);
            Long appartementId = contact.getAppartement() != null ? contact.getAppartement().getId() : null;
            return appartementId != null && currentUser.getAppartements().stream()
                .anyMatch(appartement -> appartement.getId().equals(appartementId));
        }

        // Les autres utilisateurs ne peuvent pas ajouter des contacts
        return false;
    }

    public boolean peutGererContact(Long contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Les administrateurs peuvent gérer tous les contacts
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return true;
        }

        // Les propriétaires peuvent gérer les contacts de leurs appartements
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))) {
            Optional<Contact> contact = contactRepository.findById(contactId);
            if (contact.isPresent()) {
                return appartementRepository.findAllByProprietaireNom(currentUserName).stream()
                    .anyMatch(appartement -> appartement.getContacts().contains(contact.get()));
            }
        }

        // Les autres utilisateurs (gestionnaires et viewers) n'ont pas accès aux contacts
        return false;
    }
    
    public Page<Contact> obtenirContactsAutorises(Pageable pageable) {
        Utilisateur currentUser = getCurrentUser();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            // Les administrateurs peuvent voir tous les contacts
            return contactRepository.findAll(pageable);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))) {
            // Les propriétaires peuvent voir les contacts de leurs appartements
            List<Long> appartementIds = currentUser.getAppartements().stream()
                .map(Appartement::getId)
                .collect(Collectors.toList());
            return contactRepository.findAllByAppartementIdIn(appartementIds, pageable);
        } else {
            // Les autres utilisateurs n'ont pas accès aux contacts
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }
    
    private Utilisateur getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUtilisateurDetails) {
            Long userId = ((CustomUtilisateurDetails) principal).getId();
            return utilisateurRepository.findById(userId).orElse(null);
        }

        return null; 
    }
}

