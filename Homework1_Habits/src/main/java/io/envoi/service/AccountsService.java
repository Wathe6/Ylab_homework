package io.envoi.service;

import io.envoi.enums.Roles;
import io.envoi.model.Account;

import java.util.HashMap;
import java.util.Map;
/**
 * Contains basic operations for Accounts.
 * */
public class AccountsService extends BasicService<Account> {
    public AccountsService() {
        super(new HashMap<>());
    }
    public AccountsService(Map<String, Account> accounts) {
        super(accounts);
    }
    public boolean add(Account account) {
        return account != null && items.putIfAbsent(account.getEmail(), account) == null;
    }
    public boolean create(String email, String password, Roles role) {
        if (email == null || email.isEmpty() || emailExists(email)) {
            return false;
        }
        items.put(email, new Account(email, password, role));
        return true;
    }
    public boolean update(String email, String newPassword, Roles newRole) {
        Account account = getByEmail(email);
        if (account != null) {
            account.setPassword(newPassword);
            account.setRole(newRole);
            return true;
        }
        return false;
    }
    public boolean update(Account account) {
        if (account != null && emailExists(account.getEmail())) {
            items.put(account.getEmail(), account);
            return true;
        }
        return false;
    }
}