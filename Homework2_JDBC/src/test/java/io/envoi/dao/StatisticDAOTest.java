package io.envoi.dao;

import io.envoi.config.LiquibaseConfig;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import jdk.jfr.Period;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.assertj.core.api.Assertions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;

@Testcontainers
public class StatisticDAOTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("psswd200");

    private static HabitDAO habitDAO;
    private static StatisticDAO statisticDAO;


    @BeforeAll
    public static void init() throws Exception {
        postgreSQLContainer.start();

        Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());

        LiquibaseConfig.applyMigration(connection);

        statisticDAO = new StatisticDAO();
        statisticDAO.setNewConnection(connection);
    }

    @Test
    @DisplayName("Test saving a statistic")
    public void testSaveStatistic() {
        Statistic statistic = new Statistic(1L, LocalDate.now(), true);
        boolean saved = statisticDAO.save(statistic);

        Assertions.assertThat(saved).isTrue();
        List<Statistic> statistics = statisticDAO.getByField("habit_id", 1L);
        Assertions.assertThat(statistics).isNotEmpty();
    }

    @Test
    @DisplayName("Test updating a statistic")
    public void testUpdateStatistic() {
        Statistic statistic = new Statistic(1L, LocalDate.now(), true);
        boolean saved = statisticDAO.save(statistic);
        Assertions.assertThat(saved).isTrue();

        statistic = statisticDAO.getLastDate(1L);
        statistic.setMarking(false);
        boolean updated = statisticDAO.update(statistic);
        Assertions.assertThat(updated).isTrue();

        Statistic updatedStatistic = statisticDAO.get(statistic.getId());
        Assertions.assertThat(updatedStatistic.getMarking()).isFalse();
    }

    @Test
    @DisplayName("Test getting the last statistic by habit ID")
    public void testGetLastDate() {
        Statistic statistic1 = new Statistic(1L, LocalDate.now().minusDays(1), true);
        Statistic statistic2 = new Statistic(1L, LocalDate.now(), false);
        statisticDAO.save(statistic1);
        statisticDAO.save(statistic2);

        Statistic lastStatistic = statisticDAO.getLastDate(1L);
        Assertions.assertThat(lastStatistic).isNotNull();
        Assertions.assertThat(lastStatistic.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Test deleting a statistic")
    public void testDeleteStatistic() {
        Statistic statistic = new Statistic(1L, LocalDate.now(), true);
        boolean saved = statisticDAO.save(statistic);
        Assertions.assertThat(saved).isTrue();

        statistic = statisticDAO.getLastDate(1L);
        boolean deleted = statisticDAO.delete(statistic.getId());
        Assertions.assertThat(deleted).isTrue();

        Statistic deletedStatistic = statisticDAO.get(statistic.getId());
        Assertions.assertThat(deletedStatistic).isNull();
    }

    @AfterAll
    public static void clear() {
        postgreSQLContainer.stop();
    }
}