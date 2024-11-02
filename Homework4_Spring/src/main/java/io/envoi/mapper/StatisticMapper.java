package io.envoi.mapper;

import io.envoi.model.Statistic;
import io.envoi.model.dto.StatisticDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for creating Statistic objects from resultSet.
 * */
@Mapper
public interface StatisticMapper extends BasicMapper<Statistic, StatisticDTO>
{
    StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);
    default Statistic map(ResultSet rs) throws SQLException
    {
        return new Statistic(rs);
    }
}
