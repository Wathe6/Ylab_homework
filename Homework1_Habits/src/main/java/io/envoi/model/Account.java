package io.envoi.model;

import io.envoi.enums.Roles;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
/**
 * Account is a model for users roles and credentials.
 * */
public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //Id
    private String email;
    private String password;
    private Roles role;

    public Account(String email, String password, Roles role) {
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password);
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = Objects.requireNonNull(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return email.equals(account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "Account{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}