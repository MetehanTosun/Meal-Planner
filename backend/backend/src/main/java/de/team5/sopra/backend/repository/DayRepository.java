package de.team5.sopra.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.team5.sopra.backend.models.Day;

public interface DayRepository extends JpaRepository<Day, Long>{
}
