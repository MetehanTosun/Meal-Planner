package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Weekday name;

    @ManyToOne
    @JoinColumn(name = "week_id")
    private Week week;

    @ManyToMany
    @JoinTable(
        name = "day_recipe",
        joinColumns = @JoinColumn(name = "day_id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> dayRecipes = new ArrayList<>();

    public enum Weekday {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;
    }
    public Day(){}

    public Day(Weekday name, List<Recipe> recipes) {
        this.name = name;
        this.dayRecipes = recipes;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Weekday getName() {
        return name;
    }

    public void setName(Weekday name) {
        this.name = name;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public List<Recipe> getRecipes() {
        return dayRecipes;
    }

    public void setRecipes(List<Recipe> dayRecipes) {
        this.dayRecipes = dayRecipes;
    }

    public void addRecipe(Recipe recipe) {
        this.dayRecipes.add(recipe);
    }
}
