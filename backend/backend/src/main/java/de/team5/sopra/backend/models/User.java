package de.team5.sopra.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    /*
    @OneToMany(mappedBy = "user")  // 'user' ist der Name des Feldes in der Week-Klasse, das die Beziehung speichert
    private List<Week> weeks; // Hier wird eine Liste von Week-Objekten verwendet, um die Wochen zu speichern.

     */

    public User() {

    }
}