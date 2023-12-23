package fr.quiniou.gestion_back.appartement;

import java.util.List;

import fr.quiniou.gestion_back.contact.Contact;
import fr.quiniou.gestion_back.utilisateur.UtilisateurDTO;
import lombok.Data;

@Data
public class AppartementDTO {
	private UtilisateurDTO proprietaire;
	private List<Contact> contacts;
	private List<UtilisateurDTO> gestionnaires;
	private Adresse adresse;
	private Boolean publicAppartement;
}
