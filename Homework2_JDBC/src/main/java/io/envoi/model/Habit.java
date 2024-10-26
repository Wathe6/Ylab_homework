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

    public Habit(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.accountId = rs.getLong("account_id");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
        //Check if correct
        this.period = Period.parse(rs.getString("period"));
    }
}
