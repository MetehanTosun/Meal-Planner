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
@Table(name = "weeks")
@Getter
@Setter
public class Week {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date startDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date endDate;


    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Day> days = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Week(){}

    public Week(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void addDay(Day day){
        days.add(day);
        day.setWeek(this);
    }
}
