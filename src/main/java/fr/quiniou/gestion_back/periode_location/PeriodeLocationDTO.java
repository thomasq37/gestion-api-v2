package fr.quiniou.gestion_back.periode_location;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class PeriodeLocationDTO {

	private Long id;
    private Long appartementId;
    private Double prixLocation;
    private Boolean isLocVac;
    private LocalDate estEntree;
    private LocalDate estSortie;
    private List<Long> fraisIds;

}
