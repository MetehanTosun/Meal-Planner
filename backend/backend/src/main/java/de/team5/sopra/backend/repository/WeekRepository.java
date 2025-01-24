package de.team5.sopra.backend.repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

import de.team5.sopra.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import de.team5.sopra.backend.models.Week;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeekRepository extends JpaRepository<Week, Long> {

	Week findTopByUserOrderByStartDateDesc(User user);

	@Query("SELECT w FROM Week w WHERE w.user = :user ORDER BY w.startDate DESC")
	List<Week> findByUserOrderByStartDateDesc(@Param("user") User user, Pageable pageable);

	List<Week> findByUserId(Long userId);

	List<Week> findByUser(User user);
}

