package de.team5.sopra.backend.service;

import de.team5.sopra.backend.dto.WeekRequest;
import de.team5.sopra.backend.models.*;
import de.team5.sopra.backend.repository.DayRepository;
import de.team5.sopra.backend.repository.UserRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
	 * Creates a week with a DTO
	 *
	 * It checks all the values of the request and it saves the newly created week at the end.
	 * @param weekRequest           day:ID start:Date end:Date user:ID
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
		}else{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(weekRequest.getStartDate());
			for (int i = 0; i < 7; i++) {
				Day day = new Day();
				day.setDate(calendar.getTime());
				day.setWeek(newWeek);
				newWeek.getDays().add(day);
				calendar.add(Calendar.DAY_OF_YEAR, 1);
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

	/**
	 * Retrieves the latest week via a custom query for a user or creates one if it doesn't exist
	 *
	 * If the week doesn't exist or is outDated, then it automatically creates a new Week for the user and saves it
	 *
	 * @param id        user:ID
	 * @return          week:Latest
	 */
	@Transactional
	public Week getOrCreateCurrentWeek(Long id) {
		Week latestWeek = weekRepository.findTopByUserOrderByStartDateDesc(userRepository.findById(id).get());
		if (latestWeek == null || isWeekOutdated(latestWeek)) {
			Week newWeek = createNewWeekForUser(id, new Date());
			return weekRepository.save(newWeek);
		}
		return latestWeek;
	}

	public Week createNewWeekForUser(Long id, Date startDate) {
		if (!userRepository.existsById(id)) {
			throw new IllegalArgumentException("User with id " + id + " not found");
		}
		User user = userService.getUserById(id);
		Week newWeek = new Week();
		Date adjustedStartDate = adjustDateToMonday(startDate);

		newWeek.setUser(user);
		newWeek.setStartDate(adjustedStartDate);
		newWeek.setEndDate(calculateEndDate(adjustedStartDate));

		Calendar c = Calendar.getInstance();
		c.setTime(adjustedStartDate);

		// In der for-Schleife wird com Montag aus heweils immer 1 Tag drauf addiert um das Datum aktuell zu halten

		for (int i = 0; i < 7; i++) {
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

	@Transactional
	public List<Week> getWeeksInRange(Long userId, int count) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Pageable pageable = PageRequest.of(0, count);
		List<Week> weeks = weekRepository.findByUserOrderByStartDateDesc(user, pageable);

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

	public List<Week> getWeeksByUser(User user) {
		List<Week> weeks = weekRepository.findByUser(user);

		weeks.forEach(week ->
				week.getDays().forEach(day -> {
					LocalDateTime dayEndTime = calculateLocalDateTime(day);

					List<UserSpecificRecipe> filteredRecipes = filterRecipesByDeletionStatus(day, dayEndTime);

					day.setUserSpecificRecipes(filteredRecipes);
				})
		);

		return weeks;
	}

	//                                  //
	//              HELPER              //
	//                                  //

	private LocalDateTime calculateLocalDateTime(Day day) {
		return day.getDate().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime()
				.withHour(23)
				.withMinute(59)
				.withSecond(59)
				.withNano(999999999);
	}

	private List<UserSpecificRecipe> filterRecipesByDeletionStatus(Day day, LocalDateTime dayEndTime) {
		return day.getUserSpecificRecipes().stream()
				.filter(usr -> {
					Recipe recipe = usr.getRecipe();
					if (recipe == null) {
						return false;
					}
					return !recipe.isDeleted() ||
							(recipe.getDeletedTime() != null && recipe.getDeletedTime().isAfter(dayEndTime));
				})
				.collect(Collectors.toList());
	}
	private Date adjustDateToMonday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int daysToMonday = (dayOfWeek == Calendar.SUNDAY) ? -6 : (Calendar.MONDAY - dayOfWeek);
		calendar.add(Calendar.DAY_OF_YEAR, daysToMonday);

		return calendar.getTime();
	}

	private Date calculateEndDate(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR, 6);
		return calendar.getTime();
	}

	private boolean isWeekOutdated(Week week) {
		return week.getEndDate() != null && week.getEndDate().before(new Date());
	}
}
