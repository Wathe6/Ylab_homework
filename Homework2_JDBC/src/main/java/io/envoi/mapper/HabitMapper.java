package io.envoi.mapper;

import io.envoi.model.Habit;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Mapper for creating Habit objects from resultSet.
 * */
public class HabitMapper implements Mapper<Habit>
{
    @Override
    public Habit map(ResultSet rs) throws SQLException
    {
        return new Habit(rs);
    }
}
