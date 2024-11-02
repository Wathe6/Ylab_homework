package io.envoi.dao;

import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import io.envoi.util.Queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Save, update, habitExists operations with Habits. GetAll, get(id), getByFields, delete, isTableEmpty are in BasicDAO.
 * */
public class HabitDAO extends BasicDAO<Habit, HabitDTO> {
    private static final String TABLENAME = "habits";

    public HabitDAO() {
        super(TABLENAME, HabitMapper.INSTANCE);
    }

    @Override
    public boolean save(Habit habit) {
        String sql = Queries.INSERT_HABIT;
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setLong(2, habit.getAccountId());
            ps.setString(3, habit.getName());
            ps.setString(4, habit.getDescription());
            ps.setString(5, habit.getPeriod().toString());

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
        String sql = Queries.UPDATE_HABIT;
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setLong(2, habit.getAccountId());
            ps.setString(3, habit.getName());
            ps.setString(4, habit.getDescription());
            ps.setString(5, habit.getPeriod().toString());
            ps.setLong(6, habit.getId());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }

    public boolean habitExists(Long accountId, String habitName) {
        String sql = Queries.HABITS_EXISTS;
        boolean exists = false;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setLong(2, accountId);
            ps.setString(3, habitName);

            ResultSet rs = ps.executeQuery();
            // If rs contains records, then the habitName exists
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке привычки: " + e.getMessage());
        }

        return exists;
    }
}