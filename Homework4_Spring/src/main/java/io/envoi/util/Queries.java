package io.envoi.util;

public interface Queries {
    String SELECT_ALL = "SELECT * FROM %s";
    String SELECT_WHERE = "SELECT * FROM %s WHERE id=?";
    String SELECT_BY_FIELD = "SELECT * FROM %s WHERE %s=?";
    String SELECT_ONE = "SELECT 1 FROM %s LIMIT 1";
    String DELETE = "DELETE FROM %s WHERE id=?";
    String INSERT_ACCOUNT = "INSERT INTO accounts(email, password, name, role) VALUES (?,?,?,?)";
    String INSERT_HABIT = "INSERT INTO habits(account_id, name, description, period) VALUES (?,?,?,?)";
    String INSERT_STATISTIC = "INSERT INTO statistics(habit_id, date, marking) VALUES (?,?,?)";
    String UPDATE_ACCOUNT = "UPDATE accounts SET email = ?, password = ?, name = ?, role = ? WHERE id = ?";
    String UPDATE_HABIT = "UPDATE habits SET account_id = ?, name = ?, description = ?, period = ? WHERE id = ?";
    String UPDATE_STATISTIC = "UPDATE statistics SET habit_id = ?, date = ?, marking = ? WHERE id = ?";
    String EMAIL_EXISTS = "SELECT 1 FROM accounts WHERE email=?";
    String HABITS_EXISTS = "SELECT 1 FROM habits WHERE account_id = ? AND name = ?";
    String LAST_STATISTIC = "SELECT * FROM statistics WHERE habit_id = ? ORDER BY date DESC LIMIT 1";
}