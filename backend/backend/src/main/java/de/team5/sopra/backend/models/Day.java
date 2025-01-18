package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "days")
@Getter
@Setter
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "week_id", nullable = false)
    @JsonBackReference
    private Week week;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("day-userSpecificRecipe")
    private List<UserSpecificRecipe> userSpecificRecipes = new ArrayList<>();

    public Day() {
    }

    public Day(Week week, Date date, List<UserSpecificRecipe> userSpecificRecipes) {
        this.week = week;
        this.date = date;
        userSpecificRecipes.forEach(userSpecificRecipe -> userSpecificRecipe.setDay(this));
    }

    public void addUserSpecificRecipe(UserSpecificRecipe userSpecificRecipe) {
        userSpecificRecipes.add(userSpecificRecipe);
        userSpecificRecipe.setDay(this);
    }

    public void removeUserSpecificRecipe(UserSpecificRecipe userSpecificRecipe) {
        userSpecificRecipes.remove(userSpecificRecipe);
        userSpecificRecipe.setDay(null);
    }

}
