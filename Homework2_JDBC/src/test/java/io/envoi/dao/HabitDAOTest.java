package io.envoi.dao;

import io.envoi.config.LiquibaseConfig;
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

public class HabitDAOTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("psswd200");

    private static HabitDAO habitDAO;

    @BeforeAll
    public static void init() throws Exception {
        postgreSQLContainer.start();

        Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());

        LiquibaseConfig.applyMigration(connection);

        habitDAO = new HabitDAO();
        habitDAO.setNewConnection(connection);
    }

    @Test
    @DisplayName("Test saving habit")
    public void testSaveHabit() {
        Habit newHabit = new Habit(1L, "Drink WaterTest", "Drink 8 glasses of water daily", Period.ofDays(1));

        boolean saved = habitDAO.save(newHabit);
        Assertions.assertThat(saved).isTrue();

        newHabit = habitDAO.getByField("name", "Drink WaterTest").get(0);
        Assertions.assertThat(newHabit.getId()).isNotNull();

        boolean habitExists = habitDAO.habitExists(newHabit.getAccountId(), newHabit.getName());
        Assertions.assertThat(habitExists).isTrue();
    }

    @Test
    @DisplayName("Test update habit")
    public void testUpdateHabit() {
        Habit habit = new Habit(1L, "ExerciseTest", "Exercise for 30 minutes", Period.ofDays(1));
        boolean saved = habitDAO.save(habit);
        Assertions.assertThat(saved).isTrue();

        habit = habitDAO.getByField("name", "ExerciseTest").get(0);
        habit.setDescription("Exercise for 1 hour");
        habit.setPeriod(Period.ofDays(7));

        boolean updated = habitDAO.update(habit);
        Assertions.assertThat(updated).isTrue();

        Habit updatedHabit = habitDAO.get(habit.getId());
        Assertions.assertThat(updatedHabit.getDescription()).isEqualTo("Exercise for 1 hour");
        Assertions.assertThat(updatedHabit.getPeriod()).isEqualTo(Period.ofDays(7));
    }

    @Test
    @DisplayName("Test habitExists")
    public void testHabitExists() {
        Habit habit = new Habit(1L, "Read BooksTest", "Read 20 pages daily", Period.ofDays(1));
        boolean saved = habitDAO.save(habit);
        Assertions.assertThat(saved).isTrue();

        boolean exists = habitDAO.habitExists(habit.getAccountId(), habit.getName());
        Assertions.assertThat(exists).isTrue();

        boolean nonExists = habitDAO.habitExists(habit.getAccountId(), "Non-existing Habit");
        Assertions.assertThat(nonExists).isFalse();
    }

    @Test
    public void testDeleteHabit() {
        Habit habitToDelete = new Habit(1L, "Delete Test Habit", "Test description", Period.ofDays(1));
        boolean saved = habitDAO.save(habitToDelete);
        Assertions.assertThat(saved).isTrue();

        habitToDelete = habitDAO.getByField("name", "Delete Test Habit").get(0);
        boolean deleted = habitDAO.delete(habitToDelete.getId());
        Assertions.assertThat(deleted).isTrue();

        Habit fetchedHabit = habitDAO.get(habitToDelete.getId());
        Assertions.assertThat(fetchedHabit).isNull();
    }

    @AfterAll
    public static void clean() {
        postgreSQLContainer.stop();
    }
}
