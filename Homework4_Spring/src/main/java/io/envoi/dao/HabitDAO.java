package io.envoi.dao;

import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import io.envoi.util.Queries;
import org.eclipse.core.internal.resources.mapping.ShallowResourceMapping;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Save, update, habitExists operations with Habits. GetAll, get(id), getByFields, delete, isTableEmpty are in BasicDAO.
 * */
@Repository
public class HabitDAO extends BasicDAO<Habit, HabitDTO> {
    private static final String TABLENAME = "habits";

    public HabitDAO(Connection connection) {
        super(connection, TABLENAME, HabitMapper.INSTANCE);
    }

    @Override
    public boolean save(Habit habit) {
        String sql = Queries.INSERT_HABIT;
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
        String sql = Queries.UPDATE_HABIT;
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

    public Habit get(Long accountId, String habitName) {
        String sql = Queries.HABITS_EXISTS;
        Habit result = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setString(2, habitName);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = mapper.map(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке привычки: " + e.getMessage());
        }

        return result;
    }
}