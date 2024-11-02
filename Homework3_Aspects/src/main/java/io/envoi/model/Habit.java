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
public class Habit
{
    private Long id;
    //Foreign key
    private Long accountId;
    private String name;
    private String description;
    private Period period;

    public Habit(Long accountId, String name, String description, Period period) {
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.period = period;
    }
}