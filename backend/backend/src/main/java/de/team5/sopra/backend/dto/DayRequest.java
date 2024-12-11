package de.team5.sopra.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DayRequest {
	private Date date;
	private Long weekId;
	private List<Long> recipeIds;

}

