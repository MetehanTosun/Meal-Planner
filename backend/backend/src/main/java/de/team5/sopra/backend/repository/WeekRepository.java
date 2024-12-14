package de.team5.sopra.backend.repository;
import java.util.List;

import de.team5.sopra.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import de.team5.sopra.backend.models.Week;

public interface WeekRepository extends JpaRepository<Week, Long> {

	Week findTopByUserOrderByStartDateDesc(User user);
}

