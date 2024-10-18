package io.envoi.dao;

import io.envoi.config.LiquibaseConfig;
import io.envoi.mapper.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicDAO<T>
{
    protected Connection connection = LiquibaseConfig.getDbConnection();
    protected String tableName;
    protected Mapper<T> mapper;

    public BasicDAO(String tableName, Mapper<T> mapper)
    {
        this.tableName = tableName;
        this.mapper = mapper;
    }

    public List<T> getAll()
    {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    results.add(mapper.map(rs));
                }
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return results;
    }
    /**
     * Get an entity by ID (PK)
     */
    public T get(Long id)
    {
        T result = null;
        String sql = "SELECT * FROM " + tableName + " WHERE id=?";

        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    result = mapper.map(rs);
                }
            }
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return result;
    }

    /**
     * Get a list of entities by FK
     */
    List<T> getByFk(String fkName,Long fkId)
    {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + fkName + "=?";

        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    results.add(mapper.map(rs));
                }
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return results;
    }

    abstract boolean save(T t);

    boolean delete(T t)
    {
        String sql = "DELETE FROM " + tableName + " WHERE " + "id=?";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            flag = ps.executeUpdate();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        //the flag stores the number of deleted rows
        return flag > 0;
    }

    abstract boolean update(T t);
}