package io.envoi.dao;

import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HabitDAO extends BasicDAO<Habit> {
    private static final String TABLENAME = "habits";

    public HabitDAO() {
        super(TABLENAME, new HabitMapper());
    }

    @Override
    public boolean save(Habit habit) {
        String sql = "INSERT INTO " + TABLENAME + "(account_id, name, description, period) VALUES (?,?,?,?)";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, habit.getAccountId());
            ps.setString(2, habit.getName());
            ps.setString(3, habit.getDescription());
            ps.setString(4, habit.getPeriod().toString());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was added
        return flag > 0;
    }

    @Override
    public boolean update(Habit habit) {
        String sql = "UPDATE " + TABLENAME + " SET email=?, password=?, name=?, role=? WHERE id=?";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, habit.getAccountId());
            ps.setString(2, habit.getName());
            ps.setString(3, habit.getDescription());
            ps.setString(4, habit.getPeriod().toString());
            ps.setLong(5, habit.getId());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }

    public boolean habitExists(Long accountId, String habitName) {
        String sql = "SELECT 1 FROM " + TABLENAME + " WHERE account_id=? AND name=?";
        boolean exists = false;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setString(2, habitName);

            ResultSet rs = ps.executeQuery();
            // If rs contains records, then the habitName exists
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке привычки: " + e.getMessage());
        }

        return exists;
    }
}
