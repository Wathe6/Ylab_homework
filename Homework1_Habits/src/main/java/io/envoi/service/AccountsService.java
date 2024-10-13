package io.envoi.service;

import io.envoi.enums.Roles;
import io.envoi.model.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountsService
{
    private Map<String, Account> accounts;
    public AccountsService()
    {
        accounts = new HashMap<>();
    }
    public AccountsService(Map<String, Account> accounts)
    {
        this.accounts = accounts;
    }

    public void printAll()
    {
        accounts.values().forEach(System.out::println);
    }
    public Map<String, Account> getAll()
    {
        return new HashMap<>(accounts);
    }

    public Account getByEmail(String email)
    {
        return accounts.get(email);
    }

    public boolean add(Account account)
    {
        return account != null && accounts.putIfAbsent(account.getEmail(), account) == null;
    }
    public boolean addAll(Map<String, Account> accountMap)
    {
        if(accountMap == null)
        {
            return false;
        }
        accounts.putAll(accountMap);
        return true;
    }

    public boolean create(String email, String password, Roles role)
    {
        if (email == null || email.isEmpty() || emailExists(email))
        {
            return false;
        }
        accounts.put(email, new Account(email, password, role));
        return true;
    }

    public boolean update(String email, String newPassword, Roles newRole)
    {
        Account account = getByEmail(email);
        if (account != null)
        {
            account.setPassword(newPassword);
            account.setRole(newRole);
            return true;
        }
        return false;
    }

    public boolean update(Account account)
    {
        if (account != null && emailExists(account.getEmail()))
        {
            accounts.put(account.getEmail(), account);
            return true;
        }
        return false;
    }

    public boolean delete(String email)
    {
        return accounts.remove(email) != null;
    }

    public boolean emailExists(String email)
    {
        return accounts.containsKey(email);
    }
}