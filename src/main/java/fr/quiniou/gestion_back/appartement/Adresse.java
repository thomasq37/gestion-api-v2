package fr.quiniou.gestion_back.appartement;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Adresse {
    private Integer numero;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;
}