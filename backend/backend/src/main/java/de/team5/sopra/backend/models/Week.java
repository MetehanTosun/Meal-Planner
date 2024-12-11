package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date startDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date endDate;

    
    @OneToMany(mappedBy = "week", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Day> days = new ArrayList<>();


    public Week(){}

    public void addDay(Day day){
        days.add(day);
        day.setWeek(this);
    }
}
