package fr.quiniou.gestion_back.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.quiniou.gestion_back.invitation.Invitation;
import fr.quiniou.gestion_back.invitation.InvitationRepository;
import fr.quiniou.gestion_back.role.Role;
import fr.quiniou.gestion_back.role.RoleRepository;
import fr.quiniou.gestion_back.security.CustomUtilisateurDetailsService;
import fr.quiniou.gestion_back.security.jwt.JwtUtils;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UtilisateurRepository utilisateurRepository;

	@Autowired
	InvitationRepository invitationRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private CustomUtilisateurDetailsService customUtilisateurDetailsService;

	@PostMapping("/inscription")
	public ResponseEntity<?> registerUser(@RequestBody InscriptionRequest inscriptionRequest) {
		try {
			// Vérifier le code d'invitation
			Invitation invitation = invitationRepository.findByMdp(inscriptionRequest.getCodeInvitation())
					.orElseThrow(() -> new Exception("Code d'invitation invalide ou non trouvé"));

			if (!invitation.getActive()) {
				return ResponseEntity.badRequest().body("Erreur: L'invitation n'est plus active!");
			}

			// Vérifier si le nom est déjà pris
			if (utilisateurRepository.existsByNom(inscriptionRequest.getNom())) {
				return ResponseEntity.badRequest().body("Erreur: Le nom d'utilisateur est déjà pris!");
			}

			// Création et enregistrement de l'utilisateur
			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setNom(inscriptionRequest.getNom());
			utilisateur.setMdp(passwordEncoder.encode(inscriptionRequest.getMdp()));

			// Obtenir le rôle par défaut (VIEWER)
			Role roleDefault = roleRepository.findByNom("VIEWER")
					.orElseThrow(() -> new Exception("Rôle par défaut non trouvé"));

			// Créer l'ensemble des rôles
			Set<Role> roles = new HashSet<>();
			roles.add(roleDefault);

			// Extraire la dernière lettre du code d'invitation et attribuer un rôle
			// supplémentaire si nécessaire
			String codeInvitation = inscriptionRequest.getCodeInvitation();
			char lastChar = codeInvitation.charAt(codeInvitation.length() - 1);
			if (lastChar == 'P') {
				Role roleProprietaire = roleRepository.findByNom("PROPRIETAIRE")
						.orElseThrow(() -> new Exception("Rôle PROPRIETAIRE non trouvé"));
				roles.add(roleProprietaire);
			} else if (lastChar == 'G') {
				Role roleGestionnaire = roleRepository.findByNom("GESTIONNAIRE")
						.orElseThrow(() -> new Exception("Rôle GESTIONNAIRE non trouvé"));
				roles.add(roleGestionnaire);
			}

			// Attribuer les rôles à l'utilisateur
			utilisateur.setRoles(roles);

			// Enregistrer l'utilisateur avec les rôles
			utilisateurRepository.save(utilisateur);

			// Désactiver l'invitation après utilisation
			invitation.setActive(false);
			invitationRepository.save(invitation);

			return ResponseEntity.ok("Utilisateur enregistré avec succès!");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
		}
	}

	@PostMapping("/connexion")
	public ResponseEntity<?> authenticateUser(@RequestBody Utilisateur loginRequest) {
		try {
			UserDetails userDetails = customUtilisateurDetailsService.loadUserByUsername(loginRequest.getNom());
			if (passwordEncoder.matches(loginRequest.getMdp(), userDetails.getPassword())) {
				List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList());
				String jwt = jwtUtils.generateJwtToken(userDetails.getUsername(), roles);
				return ResponseEntity.ok(jwt);
			} else {
				return ResponseEntity.badRequest().body("Erreur: Nom d'utilisateur ou mot de passe incorrect!");
			}
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.badRequest().body("Erreur: Nom d'utilisateur ou mot de passe incorrect!");
		}
	}
}