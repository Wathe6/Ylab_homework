package io.envoi.dao;

import io.envoi.mapper.AccountMapper;
import io.envoi.model.Account;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountDAO extends BasicDAO<Account>
{
    private static final String TABLENAME = "accounts";
    public AccountDAO()
    {
        super(TABLENAME, new AccountMapper());
    }

    @Override
    public boolean save(Account account)
    {
        String sql = "INSERT INTO " + TABLENAME + "(email, password, name, role) VALUES (?,?,?,?)";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, account.getEmail());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getName());
            ps.setString(4, account.getRole().toString());

            flag = ps.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was added
        return flag > 0;
    }

    @Override
    public boolean update(Account account)
    {
        String sql = "UPDATE " + TABLENAME + " SET email=?, password=?, name=?, role=? WHERE id=?";
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, account.getEmail());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getName());
            ps.setString(4, account.getRole().toString());
            ps.setLong(5, account.getId());

            flag = ps.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }
}
