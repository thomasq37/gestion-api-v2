package fr.quiniou.gestion_back.appartement.adresse;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Adresse {
    private Integer numero;
    private String voie;
    private String codePostal;
    private String ville;
    private String pays;
}