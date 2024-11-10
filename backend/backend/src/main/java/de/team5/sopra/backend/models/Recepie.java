package de.team5.sopra.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Recepie {
    
    @Id
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Foodtype foodtype;

    enum Foodtype{
        VEGAN,
        VEGETARIAN,
        MEAT
    }

}
