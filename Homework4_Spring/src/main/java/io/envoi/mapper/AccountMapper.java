package io.envoi.mapper;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.model.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for creating Account objects from resultSet.
 * */
@Mapper
public interface AccountMapper extends BasicMapper<Account, AccountDTO> {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    default Account map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String name = rs.getString("name");
        Roles role = Roles.valueOf(rs.getString("role").toUpperCase());

        return new Account(id, email, password, name, role);
    }
}