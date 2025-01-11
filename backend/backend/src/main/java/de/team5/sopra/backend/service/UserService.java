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

    public UserStatisticsDTO calculateUserStatistics(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Week> weeks = weekRepository.findByUserId(userId);
        if (weeks.isEmpty()) {
            return new UserStatisticsDTO(0, new HashMap<>(), new HashMap<>(), 0);
        }
        int totalTime = 0;
        Map<FoodType, Integer> foodTypeCounts = new HashMap<>();
        Map<String, Integer> ingredientCounts = new HashMap<>();
        int recipeCount = 0;
        for (Week week : weeks) {
            for (Day day : week.getDays()) {
                for (UserSpecificRecipe userSpecificRecipe : day.getUserSpecificRecipes()) {
                    Recipe recipe = userSpecificRecipe.getRecipe();
                    totalTime += recipe.getTime();
                    foodTypeCounts.merge(recipe.getFoodType(), 1, Integer::sum);
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        ingredientCounts.merge(ingredient.getName(), 1, Integer::sum);
                    }
                    recipeCount++;
                }
            }
        }
        return new UserStatisticsDTO(totalTime, foodTypeCounts, ingredientCounts, recipeCount);
    }
}
