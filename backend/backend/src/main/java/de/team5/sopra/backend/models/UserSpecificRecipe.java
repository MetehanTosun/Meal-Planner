package de.team5.sopra.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_recipe")
public class UserSpecificRecipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private Recipe recipe;

	@ManyToOne(fetch = FetchType.EAGER)
	private Day day;

	private int portions;

}
