package io.envoi.service;

import io.envoi.model.Account;
import io.envoi.model.Habit;
import io.envoi.model.User;

import java.util.HashMap;
import java.util.Map;

public class UsersService
{
    private Map<String, User> users;
    public UsersService()
    {
        users = new HashMap<>();
    }
    public UsersService(Map<String, User> users)
    {
        this.users = users;
    }
    public Map<String, User> getAll()
    {
        return new HashMap<>(users);
    }
    public void printALl(){}
    public User getByEmail(String email)
    {
        return users.get(email);
    }
    public Map<String, Habit> getHabits(String email)
    {
        User user = users.get(email);
        return user != null ? user.getHabits() : null;
    }
    public boolean add(User user)
    {
        return user != null && users.putIfAbsent(user.getEmail(), user) == null;
    }
    public boolean create(String email, String name)
    {
        if (email == null || email.isEmpty() || emailExists(email))
        {
            return false;
        }
        users.put(email, new User(email, name, new HashMap<>()));
        return true;
    }
    public boolean update(User user)
    {
        if (user != null && emailExists(user.getEmail()))
        {
            users.put(user.getEmail(), user);
            return true;
        }
        return false;
    }
    public boolean delete(String email)
    {
        return users.remove(email) != null;
    }
    public boolean emailExists(String email)
    {
        return users.containsKey(email);
    }
}