package io.envoi.model;

import io.envoi.enums.Roles;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Account model for database.
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account
{
    Long id;
    String email;
    String password;
    String name;
    Roles role;

    public Account(String email, String password, String name, Roles role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Account(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.email = rs.getString("email");
        this.password = rs.getString("password");
        this.name = rs.getString("name");
        String roleString = rs.getString("role").toUpperCase();
        this.role = Roles.valueOf(roleString);
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