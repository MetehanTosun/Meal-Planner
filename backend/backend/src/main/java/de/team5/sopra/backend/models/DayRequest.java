package de.team5.sopra.backend.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DayRequest {

    private Date date;               // Datum des Tages
    private Long weekId;                  // ID der Woche (optional)
    private List<Long> recipeIds;

}
