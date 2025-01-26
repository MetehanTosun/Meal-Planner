package de.team5.sopra.backend.service;

import de.team5.sopra.backend.exception.UserNotFoundException;
import de.team5.sopra.backend.models.*;
import de.team5.sopra.backend.models.enums.FoodType;
import de.team5.sopra.backend.repository.UserRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import de.team5.sopra.backend.dto.UserStatisticsDTO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WeekRepository weekRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Benutzername bereits vergeben!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public User authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username " + username + " not found"));
    }

    /**
     * Calculates user statistics, by iterating through all of the weeks the user has and applying logical calulations.
     *
     * totalTime => adding up each recipes time
     * recipeCount => increase after iterating through each recipe in a day
     * foodTypeCounts => uses merge and adds a value of 1 for each foodtype thats new and then adds up recurring
     *                   ones in the map
     * ingredientCounts => uses a similar strategy to foodType, just that it checks for the name
     * weeklyCookingTimes => assigns the weekly time that one spend cooking, by adding up all the recipeTimes of a week
     *                       and reseting after a full week iteration before the next one
     * recipeFrequency =>uses the same strat as ingreadientCounts with recipe names
     * averageTime => add up all the recipes in the list and divide totalTime / recipeCount
     *
     * @param userId
     * @return
     */
    public UserStatisticsDTO calculateUserStatistics(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Week> weeks = weekRepository.findByUserId(userId);
        if (weeks.isEmpty()) {
            return new UserStatisticsDTO(0, new HashMap<>(), new HashMap<>(), 0, new HashMap<>(), 0, new HashMap<>());
        }

        int totalTime = 0;
        Map<FoodType, Integer> foodTypeCounts = new HashMap<>();
        Map<String, Integer> ingredientCounts = new HashMap<>();
        Map<String, Integer> weeklyCookingTimes = new HashMap<>();
        Map<String, Integer> recipeFrequency = new HashMap<>();
        int recipeCount = 0;

        for (Week week : weeks) {
            int weeklyTime = 0;
            for (Day day : week.getDays()) {
                LocalDateTime dayEndTime = calculateLocalDateTime(day);
                for (UserSpecificRecipe userSpecificRecipe : day.getUserSpecificRecipes()) {
                    Recipe recipe = userSpecificRecipe.getRecipe();
                    if (recipe.isDeleted() && recipe.getDeletedTime() != null && recipe.getDeletedTime().isBefore(dayEndTime)){
                        continue;
                    }

                    int recipeTime = recipe.getTime();
                    totalTime += recipeTime;
                    weeklyTime += recipeTime;

                    foodTypeCounts.merge(recipe.getFoodType(), 1, Integer::sum);

                    for (Ingredient ingredient : recipe.getIngredients()) {
                        ingredientCounts.merge(ingredient.getName(), 1, Integer::sum);
                    }

                    recipeFrequency.merge(recipe.getName(), 1, Integer::sum);
                    recipeCount++;
                }
            }

            String weekLabel = week.getStartDate().toString();
            weeklyCookingTimes.put(weekLabel, weeklyTime);
        }

        int averageTimePerRecipe = calculateAverageTimePerRecipe(totalTime, recipeCount);

        return new UserStatisticsDTO(
                totalTime,
                foodTypeCounts,
                ingredientCounts,
                recipeCount,
                weeklyCookingTimes,
                averageTimePerRecipe,
                recipeFrequency
        );
    }

    private int calculateAverageTimePerRecipe(int totalTime, int recipeCount) {
        return recipeCount > 0 ? totalTime / recipeCount : 0;
    }

    private LocalDateTime calculateLocalDateTime(Day day) {
        return day.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
    }

}
