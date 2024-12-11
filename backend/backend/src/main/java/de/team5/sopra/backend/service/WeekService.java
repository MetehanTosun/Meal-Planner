package de.team5.sopra.backend.service;

import de.team5.sopra.backend.dto.WeekRequest;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.repository.DayRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import org.hibernate.query.IllegalQueryOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeekService {
	@Autowired
	private WeekRepository weekRepository;
	@Autowired
	private DayRepository dayRepository;

	public List<Week> getAllWeeks() {
		return weekRepository.findAll();
	}

	public Week createWeek(WeekRequest week) {
		Week newWeek = new Week();
		System.out.println(week.getStartDate());
		System.out.println(week.getEndDate());
		System.out.println(week.getDays());
		newWeek.setStartDate(week.getStartDate());
		newWeek.setEndDate(week.getEndDate());
		if(week.getDays() != null) {
			for(Long dayId : week.getDays()) {
				if(dayRepository.findById(dayId).isPresent()) {
					Day day = dayRepository.findById(dayId).get();
					newWeek.addDay(day);
				}else{
					throw new IllegalArgumentException("Day value passed does not exist!");
				}
			}
		}
		return weekRepository.save(newWeek);
	}
}
