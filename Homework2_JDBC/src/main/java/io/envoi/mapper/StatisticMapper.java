package io.envoi.mapper;

import io.envoi.model.Statistic;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticMapper implements Mapper<Statistic>
{
    @Override
    public Statistic map(ResultSet rs) throws SQLException
    {
        return new Statistic(rs);
    }
}
