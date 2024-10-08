package io.envoi.service;

import io.envoi.enums.Roles;
import io.envoi.model.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountsService {
    private Map<String, Account> accounts = new HashMap<>();

    public void printAll() {
        accounts.values().forEach(System.out::println);
    }

    public Account getByEmail(String email) {
        return accounts.get(email);
    }

    public Account getByName(String name) {
        return accounts.values().stream()
                .filter(account -> account.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean add(Account account) {
        if (account != null && !emailExists(account.getEmail())) {
            accounts.put(account.getEmail(), account);
            return true;
        }
        return false;
    }

    public boolean create(String name, String email, String password, Roles role) {
        if (email != null && !email.isEmpty() && !emailExists(email)) {
            accounts.put(email, new Account(name, email, password, role));
            return true;
        }
        return false;
    }

    public boolean update(String email, String newName, String newPassword, Roles newRole) {
        Account account = getByEmail(email);
        if (account != null) {
            if (newName != null && !newName.isEmpty()) {
                account.setName(newName);
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                account.setPassword(newPassword);
            }
            if (newRole != null) {
                account.setRole(newRole);
            }
            return true;
        }
        return false;
    }

    public boolean delete(String email) {
        return accounts.remove(email) != null;
    }

    public boolean emailExists(String email) {
        return accounts.containsKey(email);
    }
}