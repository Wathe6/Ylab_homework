package io.envoi.dao;

import io.envoi.config.LiquibaseConfig;
import io.envoi.mapper.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * GetAll, get(id), getByFields, delete, isTableEmpty are basic operations for another DAO. T must be a model class.
 * */
public abstract class BasicDAO<T> {
    protected Connection connection = LiquibaseConfig.getDbConnection();
    protected String tableName;
    protected Mapper<T> mapper;

    public BasicDAO(String tableName, Mapper<T> mapper) {
        this.tableName = tableName;
        this.mapper = mapper;
    }

    public List<T> getAll() {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (Exception e) {
            System.out.printf("Ошибка при получении всех записей в таблице %s: %s%n", tableName, e.getMessage());
        }

        return results;
    }
    /**
     * Get an entity by ID (PK)
     */
    public T get(Long id) {
        T result = null;
        String sql = "SELECT * FROM " + tableName + " WHERE id=?";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = mapper.map(rs);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    /**
     * Get a list of entities by any field
     */
    public <V> List<T> getByField(String fieldName, V value) {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + fieldName + "=?";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (Exception e) {
            System.out.printf("Ошибка при получении записей по полю %s в таблице %s: %s%n",
                    fieldName,  tableName, e.getMessage());
        }

        return results;
    }

    public abstract boolean save(T t);

    public <V extends  Long> boolean delete(V v) {
        String sql = "DELETE FROM " + tableName + " WHERE " + "id=?";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, v);
            flag = ps.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            System.out.printf("Ошибка при попытке удалить запись в таблице %s: %s%n", tableName, e.getMessage());
        }
        //the flag stores the number of deleted rows
        return flag > 0;
    }

    public abstract boolean update(T t);

    public boolean isTableEmpty() {
        String sql = "SELECT 1 FROM " + tableName + " LIMIT 1";
        boolean isEmpty = true;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
             ResultSet rs = ps.executeQuery();
             // If rs contains records, then the table is not empty
             isEmpty = !rs.next();
        } catch (SQLException e) {
            System.out.printf("Ошибка при проверке пустоты таблицы %s: %s%n", tableName, e.getMessage());
        }

        return isEmpty;
    }

    public void setNewConnection(Connection connection) {
        this.connection = connection;
    }
}