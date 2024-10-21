package io.envoi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Mapper for creating objects from resultSet.
 * */
public interface Mapper<T>
{
    T map(ResultSet rs) throws SQLException;
}
