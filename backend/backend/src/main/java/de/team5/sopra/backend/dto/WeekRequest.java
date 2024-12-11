package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.Day;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class WeekRequest {

	private List<Long> days;
	private Date startDate;
	private Date endDate;
}
