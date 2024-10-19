package io.envoi.dao;

import io.envoi.mapper.StatisticMapper;
import io.envoi.model.Statistic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticDAO extends BasicDAO<Statistic>
{
    private static final String TABLENAME = "statistics";
    public StatisticDAO() {
        super(TABLENAME, new StatisticMapper());
    }

    @Override
    public boolean save(Statistic statistic) {
        String sql = "INSERT INTO " + TABLENAME + "(habit_id, date, marking) VALUES (?,?,?)";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, statistic.getHabitId());

            java.sql.Date sqlDate = java.sql.Date.valueOf(statistic.getDate());
            ps.setDate(2, sqlDate);

            if (statistic.getMarking() == null) {
                ps.setNull(3, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(3, statistic.getMarking());
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
        String sql = "UPDATE " + TABLENAME + " SET habit_id=?, date=?, marking=? WHERE id=?";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, statistic.getHabitId());
            ps.setString(2, statistic.getDate().toString());
            if (statistic.getMarking() == null) {
                ps.setNull(3, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(3, statistic.getMarking());
            }
            ps.setLong(4, statistic.getId());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }
    public Statistic getLastDate(Long habitId) {
        String sql = "SELECT * FROM " + TABLENAME + " WHERE habit_id = ? ORDER BY date DESC LIMIT 1";
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