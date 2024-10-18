package io.envoi.model;

import io.envoi.enums.Roles;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public Account(ResultSet rs) throws SQLException
    {
        this.id = rs.getLong("id");
        this.email = rs.getString("email");
        this.password = rs.getString("password");
        this.name = rs.getString("name");
        this.role = Roles.valueOf(rs.getString("role"));
    }

    public String toString()
    {
        return "Account{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}