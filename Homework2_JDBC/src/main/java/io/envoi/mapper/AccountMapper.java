package io.envoi.mapper;

import io.envoi.model.Account;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Mapper for creating Account objects from resultSet.
 * */
public class AccountMapper implements Mapper<Account>
{
    @Override
    public Account map(ResultSet rs) throws SQLException
    {
        return new Account(rs);
    }
}
