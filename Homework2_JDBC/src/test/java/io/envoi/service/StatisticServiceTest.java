package io.envoi.service;

import io.envoi.config.LiquibaseConfig;
import io.envoi.dao.HabitDAO;
import io.envoi.dao.StatisticDAO;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class StatisticServiceTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("psswd200");

    private StatisticService statisticService;
    private HabitDAO habitDAO;
    private Habit testHabit;

    @BeforeEach
    public void init() throws Exception {
        postgreSQLContainer.start();

        Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());

        LiquibaseConfig.applyMigration(connection);

        habitDAO = new HabitDAO();
        habitDAO.setNewConnection(connection);
        statisticService = new StatisticService(new StatisticDAO());
        statisticService.dao.setNewConnection(connection);

        // Create a test habit for statistic tests
        testHabit = new Habit(1L, "Test Habit", "Test Description", Period.ofDays(1));
        habitDAO.save(testHabit);
        testHabit = habitDAO.getByField("name", "Test Habit").get(0);
    }

    @Test
    @DisplayName("Test saving statistic")
    public void testSaveStatistic() {
        Statistic statistic = new Statistic(testHabit.getId(), LocalDate.now(), true);

        boolean saved = statisticService.save(statistic);
        Assertions.assertThat(saved).isTrue();

        List<Statistic> statistics = statisticService.getByHabitId(testHabit.getId());
        Assertions.assertThat(statistics).isNotNull();
    }

    @Test
    @DisplayName("Test getLastStatistic")
    public void testGetLastStatistic() {
        Statistic statistic = new Statistic(testHabit.getId(), LocalDate.now().minusDays(1), false);
        statisticService.save(statistic);

        Statistic lastStatistic = statisticService.getLastStatistic(testHabit.getId());
        Assertions.assertThat(lastStatistic).isNotNull();
        Assertions.assertThat(lastStatistic.getMarking()).isFalse();
    }

    @AfterAll
    public static void clear() {
        postgreSQLContainer.stop();
    }
}
