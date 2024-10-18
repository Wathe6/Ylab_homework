package io.envoi.dao;

import io.envoi.mapper.StatisticMapper;
import io.envoi.model.Statistic;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatisticDAO extends BasicDAO<Statistic>
{
    private static final String TABLENAME = "statistics";
    public StatisticDAO()
    {
        super(TABLENAME, new StatisticMapper());
    }

    @Override
    boolean save(Statistic statistic)
    {
        String sql = "INSERT INTO " + TABLENAME + "(habit_id, date, marking) VALUES (?,?,?)";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, statistic.getHabitId());
            ps.setString(2, statistic.getDate().toString());
            ps.setBoolean(3, statistic.getMarking());

            flag = ps.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was added
        return flag > 0;
    }
    @Override
    boolean update(Statistic statistic)
    {
        String sql = "UPDATE " + TABLENAME + " SET habit_id=?, date=?, marking=? WHERE id=?";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, statistic.getHabitId());
            ps.setString(2, statistic.getDate().toString());
            ps.setBoolean(3, statistic.getMarking());
            ps.setLong(4, statistic.getId());

            flag = ps.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }
}
