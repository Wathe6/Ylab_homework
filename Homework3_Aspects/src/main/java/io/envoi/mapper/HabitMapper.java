package io.envoi.mapper;

import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Period;

/**
 * Mapper for creating Habit objects from resultSet.
 * */
@Mapper
public interface HabitMapper extends BasicMapper<Habit, HabitDTO>
{
    HabitMapper INSTANCE = Mappers.getMapper(HabitMapper.class);
    default Habit map(ResultSet rs) throws SQLException
    {
        Long id = rs.getLong("id");
        Long accountId = rs.getLong("account_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        //Check if correct
        Period period = Period.parse(rs.getString("period"));

        return new Habit(id, accountId, name, description, period);
    }
}
