package io.envoi.dao;

import io.envoi.mapper.AccountMapper;
import io.envoi.model.Account;
import io.envoi.model.dto.AccountDTO;
import io.envoi.util.Queries;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Save, update, emailExists operations with Account. GetAll, get(id), getByFields, delete, isTableEmpty are in BasicDAO.
 * */
@Repository
public class AccountDAO extends BasicDAO<Account, AccountDTO> {
    private static final String TABLENAME = "accounts";
    public AccountDAO(Connection connection) {
        super(connection, TABLENAME, AccountMapper.INSTANCE);
    }

    @Override
    public boolean save(Account account) {
        String sql = Queries.INSERT_ACCOUNT;
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, account.getEmail());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getName());
            ps.setString(4, account.getRole().toString());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was added
        return flag > 0;
    }

    @Override
    public boolean update(Account account) {
        String sql = Queries.UPDATE_ACCOUNT;
        int flag = 0;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, account.getEmail());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getName());
            ps.setString(4, account.getRole().toString());
            ps.setLong(5, account.getId());

            flag = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //The flag stores 1 if row was updated
        return flag > 0;
    }

    public boolean emailExists(String email) {
        String sql = Queries.EMAIL_EXISTS;
        boolean exists = false;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            // If rs contains records, then the email exists
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске email: " + e.getMessage());
        }

        return exists;
    }
}