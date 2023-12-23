package fr.quiniou.gestion_back.auth;

public class InscriptionRequest {
	private String nom;
    private String mdp;  
    private String email;
    public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
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
	public String getCodeInvitation() {
		return codeInvitation;
	}
	public void setCodeInvitation(String codeInvitation) {
		this.codeInvitation = codeInvitation;
	}
	private String telNumero;
    private String codeInvitation;
}
