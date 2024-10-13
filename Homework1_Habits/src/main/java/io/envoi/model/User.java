package io.envoi.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class User implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    //Foreign key
    private String email;
    private String name;
    /*
    * Contains the habit name as key, and habit itself as the value.
    * */
    private Map<String, Habit> habits;

    public User(String email, String name, Map<String, Habit> habits)
    {
        setEmail(email);
        setName(name);
        setHabits(habits);
    }
    public List<Habit> getHabitsToMark()
    {
        return habits.values().stream()
                .filter(Habit::canBeChecked)
                .collect(Collectors.toList());
    }
    public boolean addHabit(Habit habit)
    {
        return habit != null && habits.putIfAbsent(habit.getName(), habit) == null;
    }

    public Habit findHabit(String name)
    {
        return habits.get(name);
    }
    public boolean removeHabit(Habit habit)
    {
        return habit != null && habits.remove(habit.getName()) != null;
    }

    public boolean containsHabit(Habit habit)
    {
        return habit != null && habits.containsKey(habit.getName());
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = Objects.requireNonNull(email);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = Objects.requireNonNull(name);
    }

    public Map<String, Habit> getHabits()
    {
        return habits;
    }

    public void setHabits(Map<String, Habit> habits)
    {
        this.habits = Objects.requireNonNull(habits);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(email);
    }

    @Override
    public String toString()
    {
        return "Person{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", habits=" + habits +
                '}';
    }
}