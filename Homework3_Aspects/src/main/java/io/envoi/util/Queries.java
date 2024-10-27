package io.envoi.util;

public interface Queries {
    String SELECT_ALL = "SELECT * FROM ?";
    String SELECT_WHERE = "SELECT * FROM ? WHERE id=?";
    String SELECT_BY_FIELD = "SELECT * FROM ? WHERE ?=?";
    String SELECT_ONE = "SELECT 1 FROM ? LIMIT 1";
    String DELETE = "DELETE FROM ? WHERE id=?";
    String INSERT_ACCOUNT = "INSERT INTO ?(email, password, name, role) VALUES (?,?,?,?)";
    String INSERT_HABIT = "INSERT INTO ?(account_id, name, description, period) VALUES (?,?,?,?)";
    String INSERT_STATISTIC = "INSERT INTO ?(habit_id, date, marking) VALUES (?,?,?)";
    String UPDATE_ACCOUNT = "UPDATE ? SET email=?, password=?, name=?, role=? WHERE id=?";
    String UPDATE_HABIT = "UPDATE ? SET account_id=?, name=?, description=?, period=? WHERE id=?";
    String UPDATE_STATISTIC = "UPDATE ? SET habit_id=?, date=?, marking=? WHERE id=?";
    String EMAIL_EXISTS = "SELECT 1 FROM ? WHERE email=?";
    String HABITS_EXISTS = "SELECT 1 FROM ? WHERE account_id=? AND name=?";
    String LAST_STATISTIC = "SELECT * FROM ? WHERE habit_id = ? ORDER BY date DESC LIMIT 1";
}