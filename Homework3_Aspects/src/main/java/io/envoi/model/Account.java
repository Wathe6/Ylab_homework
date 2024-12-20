package io.envoi.model;

import io.envoi.enums.Roles;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Account model for database.
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account
{
    private Long id;
    private String email;
    private String password;
    private String name;
    private Roles role;

    public Account(String email, String password, String name, Roles role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}