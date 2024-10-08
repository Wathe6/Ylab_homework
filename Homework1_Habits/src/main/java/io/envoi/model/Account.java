package io.envoi.model;

import io.envoi.enums.Roles;

import java.util.Objects;

public class Account
{
    private String name;
    private String email;
    private String password;
    private Roles role;

    public Account(String name, String email, String password, Roles role)
    {
        setName(name);
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = Objects.requireNonNull(name);
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = Objects.requireNonNull(email);
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = Objects.requireNonNull(password);
    }

    public Roles getRole()
    {
        return role;
    }

    public void setRole(Roles role)
    {
        this.role = Objects.requireNonNull(role);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return email.equals(account.email);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, email, password);
    }

    @Override
    public String toString()
    {
        return "Account{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
