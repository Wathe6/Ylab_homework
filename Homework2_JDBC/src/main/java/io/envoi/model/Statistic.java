package io.envoi.model;

import java.time.LocalDate;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Statistic model for database.
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistic
{
    Long id;
    //Foreign key
    Long habitId;
    LocalDate date;
    Boolean marking;
    public Statistic(Long habitId, LocalDate localDate, Boolean marking) {
        this.habitId = habitId;
        this.date = localDate;
        this.marking = marking;
    }
    public Statistic(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.habitId = rs.getLong("habit_id");
        this.date = rs.getDate("date").toLocalDate();
        this.marking = rs.getBoolean("marking");
    }
}
