package io.envoi.service;

import io.envoi.model.Habit;
import io.envoi.model.User;

import java.util.HashMap;
import java.util.Map;
/**
 * Contains basic operations for User.
 * */
public class UsersService extends BasicService<User> {
    public UsersService()
    {
        super(new HashMap<>());
    }
    public UsersService(Map<String, User> users)
    {
        super(users);
    }
    public Map<String, Habit> getHabits(String email) {
        User user = items.get(email);
        return user != null ? user.getHabits() : null;
    }
    public boolean add(User user) {
        return user != null && items.putIfAbsent(user.getEmail(), user) == null;
    }
    public boolean create(String email, String name) {
        if (email == null || email.isEmpty() || emailExists(email)) {
            return false;
        }
        items.put(email, new User(email, name, new HashMap<>()));
        return true;
    }
    public boolean update(User user) {
        if (user != null && emailExists(user.getEmail())) {
            items.put(user.getEmail(), user);
            return true;
        }
        return false;
    }
}