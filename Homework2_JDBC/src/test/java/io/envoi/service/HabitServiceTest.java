package io.envoi.service;

import io.envoi.config.LiquibaseConfig;
import io.envoi.dao.AccountDAO;
import io.envoi.dao.HabitDAO;
import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.model.Habit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Period;
import java.util.List;

public class HabitServiceTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("psswd200");

    private static HabitService habitService;
    private static AccountDAO accountDAO;
    private static Account testAccount;

    @BeforeAll
    public static void init() throws Exception {
        postgreSQLContainer.start();

        Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());

        LiquibaseConfig.applyMigration(connection);

        HabitDAO habitDAO = new HabitDAO();
        habitDAO.setNewConnection(connection);
        accountDAO = new AccountDAO();
        accountDAO.setNewConnection(connection);

        habitService = new HabitService(habitDAO);

        testAccount = new Account("habit_test@example.com", "password123", "John Habit", Roles.USER);
        accountDAO.save(testAccount);
        testAccount = accountDAO.getByField("email", "habit_test@example.com").get(0);
    }

    @Test
    @DisplayName("Test saving habit")
    public void testSaveHabit() {
        Habit newHabit = new Habit(testAccount.getId(), "Morning Exercise", "Exercise every morning", Period.ofDays(1));

        boolean saved = habitService.save(newHabit);
        Assertions.assertThat(saved).isTrue();

        List<Habit> habits = habitService.getByAccountId(testAccount.getId());
        Assertions.assertThat(habits).isNotNull();
    }

    @Test
    @DisplayName("Test habitExists")
    public void testHabitExists() {
        Habit newHabit = new Habit(testAccount.getId(), "Evening Meditation", "Meditate every evening", Period.ofDays(1));
        habitService.save(newHabit);

        boolean exists = habitService.habitExists(testAccount.getId(), "Evening Meditation");
        Assertions.assertThat(exists).isTrue();

        boolean nonExists = habitService.habitExists(testAccount.getId(), "Non-existing Habit");
        Assertions.assertThat(nonExists).isFalse();
    }

    @Test
    @DisplayName("Test getByAccountId")
    public void testGetByAccountId() {
        Habit newHabit = new Habit(testAccount.getId(), "Weekly Reading", "Read every week", Period.ofDays(7));
        habitService.save(newHabit);

        List<Habit> habits = habitService.getByAccountId(testAccount.getId());
        Assertions.assertThat(habits).hasSize(2); // One from previous test and this one
    }

    @AfterAll
    public static void clear() {
        postgreSQLContainer.stop();
    }
}
