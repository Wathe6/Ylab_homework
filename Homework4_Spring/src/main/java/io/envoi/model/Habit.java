package io.envoi.model;

import java.time.Period;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Habit model for database.
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Habit {
    Long id;
    //Foreign key
    Long accountId;
    String name;
    String description;
    Period period;

    public Habit(Long accountId, String name, String description, Period period) {
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.period = period;
    }
}