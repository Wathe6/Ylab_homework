package io.envoi.mapper;

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
        return new Account(rs);
    }
}