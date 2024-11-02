package io.envoi.service;

import io.envoi.dao.AccountDAO;
import io.envoi.model.Account;
import io.envoi.model.dto.AccountDTO;

import java.util.List;

/**
 * GetByEmail, emailExists operations with Account. GetAll, get(id), delete, update, isTableEmpty are in BasicService.
 * */
public class AccountService extends BasicService<Account, AccountDTO> {

    public AccountService(AccountDAO dao) {
        super(dao);
    }
    /**
     * Email is a unique field
     * */
    public Account getByEmail(String email) {
        List<Account> list = dao.getByField("email", email);
        if(list == null || list.isEmpty())
        {
            System.out.println("Ошибка при поиске email - его не существует.");
            return null;
        }
        return list.get(0);
    }

    public boolean emailExists(String email) {
        return ((AccountDAO) dao).emailExists(email);
    }

    /**
     * Instead of updateName:Email:Password use just update.
     */
}