package io.envoi.model;

import java.time.LocalDate;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Statistic model for database.
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {
    private Long id;
    //Foreign key
    private Long habitId;
    private LocalDate date;
    private Boolean marking;
    public Statistic(Long habitId, LocalDate localDate, Boolean marking) {
        this.habitId = habitId;
        this.date = localDate;
        this.marking = marking;
    }
}
