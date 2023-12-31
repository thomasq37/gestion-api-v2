package fr.quiniou.gestion_back.utilisateur.dto;

public class UtilisateurDTO {
    private String nom;
    private String email;
    private String telNumero;

    // Constructeur, getters et setters

    public UtilisateurDTO(String nom, String email, String telNumero) {
        this.nom = nom;
        this.email = email;
        this.telNumero = telNumero;
    }
    
    

    public UtilisateurDTO() {
	}



	// Getters et setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelNumero() {
        return telNumero;
    }

    public void setTelNumero(String telNumero) {
        this.telNumero = telNumero;
    }
}
