package io.envoi.mapper;

import io.envoi.model.Statistic;
import io.envoi.model.dto.StatisticDTO;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Mapper for creating Statistic objects from resultSet.
 * */
public interface StatisticMapper extends Mapper<Statistic, StatisticDTO>
{
    StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);
    default Statistic map(ResultSet rs) throws SQLException
    {
        return new Statistic(rs);
    }
}
