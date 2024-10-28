package io.envoi.mapper;

import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for creating Habit objects from resultSet.
 * */
@Mapper
public interface HabitMapper extends BasicMapper<Habit, HabitDTO>
{
    HabitMapper INSTANCE = Mappers.getMapper(HabitMapper.class);
    default Habit map(ResultSet rs) throws SQLException
    {
        return new Habit(rs);
    }
}
