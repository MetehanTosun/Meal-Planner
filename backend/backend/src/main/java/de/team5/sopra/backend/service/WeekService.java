package de.team5.sopra.backend.service;

import de.team5.sopra.backend.dto.WeekRequest;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.repository.DayRepository;
import de.team5.sopra.backend.repository.UserRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WeekService {

	@Autowired
	private WeekRepository weekRepository;
	@Autowired
	private DayRepository dayRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;

	public List<Week> getAllWeeks() {
		return weekRepository.findAll();
	}

	/**
	 * Creates a new Week from WeekRequest and always enforces a valid User ID.
	 */
	public Week createWeek(WeekRequest weekRequest) {
		if (weekRequest.getUserId() == null) {
			throw new IllegalArgumentException("A userId is required to create a Week.");
		}

		User user = userRepository.findById(weekRequest.getUserId())
				.orElseThrow(() -> new IllegalArgumentException(
						"User not found with id: " + weekRequest.getUserId()));

		Week newWeek = new Week();
		newWeek.setUser(user);
		newWeek.setStartDate(weekRequest.getStartDate());
		newWeek.setEndDate(weekRequest.getEndDate());

		if (weekRequest.getDays() != null) {
			for (Long dayId : weekRequest.getDays()) {
				Day day = dayRepository.findById(dayId)
						.orElseThrow(() -> new IllegalArgumentException(
								"Day with ID " + dayId + " does not exist!"));
				newWeek.addDay(day);
			}
		}

		return weekRepository.save(newWeek);
	}

	public Optional<Week> getWeekById(Long id) {
		return weekRepository.findById(id);
	}

	public void deleteWeek(Long id) {
		if (!weekRepository.existsById(id)) {
			throw new IllegalArgumentException("Week with id " + id + " not found");
		}
		weekRepository.deleteById(id);
	}

	@Transactional
	public Week getOrCreateCurrentWeek(Long id) {
		// TODO: Implement Check
		Week latestWeek = weekRepository.findTopByUserOrderByStartDateDesc(userRepository.findById(id).get());
		if (latestWeek == null || isWeekOutdated(latestWeek)) {
			Week newWeek = createNewWeekForUser(id, new Date());
			return weekRepository.save(newWeek);
		}
		return latestWeek;
	}

	private boolean isWeekOutdated(Week week) {
		return week.getEndDate() != null && week.getEndDate().before(new Date());
	}

	public Week createNewWeekForUser(Long id, Date startDate) {
		if (!userRepository.existsById(id)) {
			throw new IllegalArgumentException("User with id " + id + " not found");
		}
		User user = userService.getUserById(id);
		Week newWeek = new Week();
		newWeek.setUser(user);
		newWeek.setStartDate(startDate);
		newWeek.setEndDate(calculateEndDate(startDate));

		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		for (int i = 0; i < 7; i++) {
			System.out.println("Creating the days");
			Day day = new Day();
			day.setDate(c.getTime());
			day.setWeek(newWeek);
			newWeek.getDays().add(day);
			c.add(Calendar.DAY_OF_YEAR, 1);
		}

		Week savedWeek = weekRepository.save(newWeek);

		user.getWeeks().add(savedWeek);
		userRepository.save(user);
		return savedWeek;
	}

	private Date calculateEndDate(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR, 6);
		return calendar.getTime();
	}

	@Transactional
	public Week addDayToWeek(Long weekId, Long dayId) {
		Week week = weekRepository.findById(weekId)
				.orElseThrow(() -> new IllegalArgumentException("Week with id " + weekId + " not found."));
		Day day = dayRepository.findById(dayId)
				.orElseThrow(() -> new IllegalArgumentException("Day with id " + dayId + " not found."));

		if (week.getDays().contains(day)) {
			throw new IllegalArgumentException("Day is already part of the Week.");
		}
		day.setWeek(week);
		dayRepository.save(day);

		return week;
	}

	@Transactional
	public List<Week> getWeeksInRange(Long userId, int count) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Pageable pageable = PageRequest.of(0, count);
		List<Week> weeks = weekRepository.findByUserOrderByStartDateDesc(user, pageable);

		System.out.println("Fetched weeks: " + weeks);

		Collections.reverse(weeks);

		while (weeks.size() < count) {
			System.out.println("Adding new week");
			Week newWeek = createNewWeekBasedOnLast(userId);
			weeks.add(newWeek);
		}

		return weeks;
	}

	@Transactional
	public Week createNewWeekBasedOnLast(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Week latestWeek = weekRepository.findTopByUserOrderByStartDateDesc(user);

		Date newStartDate;
		if (latestWeek != null && latestWeek.getEndDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(latestWeek.getEndDate());
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			newStartDate = calendar.getTime();
		} else {
			newStartDate = new Date();
		}

		Week newWeek = createNewWeekForUser(userId, newStartDate);

		return weekRepository.save(newWeek);
	}
}
