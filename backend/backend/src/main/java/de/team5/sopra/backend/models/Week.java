package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "weeks")
@Getter
@Setter
public class Week {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * For later use, when we want to look back on what we ate
     */
    private Date date;
    private Date startDate;
    private Date endDate;

    
    @OneToMany(mappedBy = "week", cascade = CascadeType.PERSIST)
    private List<Day> days;

    public Week(){}

}
