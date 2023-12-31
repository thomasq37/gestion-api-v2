package fr.quiniou.gestion_back.contact.dto;

import lombok.Data;

@Data
public class ContactDTOForAdmin {

	private Long id;
	private String nom;
	private String email;
	private String telNumero;
	private Long appartementId;
}
