package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @JoinColumn(name = "week_id")
    private Week week;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "day_recipe",
            joinColumns = @JoinColumn(name = "day_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> recipes = new ArrayList<>();

    public Day() {

    }

}
