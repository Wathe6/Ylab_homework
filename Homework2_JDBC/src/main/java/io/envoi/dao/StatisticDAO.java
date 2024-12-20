package io.envoi.dao;

import io.envoi.mapper.StatisticMapper;
import io.envoi.model.Statistic;
import io.envoi.util.Queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Save, update, getLastDate operations with Statistic. GetAll, get(id), getByFields, delete, isTableEmpty are in BasicDAO.
 * */
public class StatisticDAO extends BasicDAO<Statistic>
{
    private static final String TABLENAME = "statistics";
    public StatisticDAO() {
        super(TABLENAME, new StatisticMapper());
    }

    @Override
    public boolean save(Statistic statistic) {
        String sql = Queries.INSERT_STATISTIC;
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setLong(2, statistic.getHabitId());

            java.sql.Date sqlDate = java.sql.Date.valueOf(statistic.getDate());
            ps.setDate(3, sqlDate);

            if (statistic.getMarking() == null) {
                ps.setNull(4, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(4, statistic.getMarking());
            }

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was added
        return flag > 0;
    }
    @Override
    public boolean update(Statistic statistic) {
        String sql = Queries.UPDATE_STATISTIC;
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setLong(2, statistic.getHabitId());

            java.sql.Date sqlDate = java.sql.Date.valueOf(statistic.getDate());
            ps.setDate(3, sqlDate);

            if (statistic.getMarking() == null) {
                ps.setNull(4, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(4, statistic.getMarking());
            }
            ps.setLong(5, statistic.getId());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }
    public Statistic getLastDate(Long habitId) {
        String sql = Queries.LAST_STATISTIC;
        Statistic statistic = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, habitId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    statistic = new Statistic(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return statistic;
    }
}