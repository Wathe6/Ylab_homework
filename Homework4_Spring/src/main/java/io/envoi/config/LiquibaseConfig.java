package io.envoi.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * Database config file. Creates liquibase tables in public and my tables in habits_schema.
 * */
@Configuration
public class LiquibaseConfig
{
    private static Connection dbConnection;
    private static String changeLogFile;
    private static String initLogFile;
    private static String url;
    private static String username;
    private static String password;

    static {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlData;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream fis = classLoader.getResourceAsStream("application.yaml")) {
            yamlData = yaml.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yaml", e);
        }

        Map<String, Object> databaseProperties = (Map<String, Object>) yamlData.get("database");
        url = (String) databaseProperties.get("url");
        username = (String) databaseProperties.get("username");
        password = (String) databaseProperties.get("password");
        changeLogFile = (String) databaseProperties.get("changeLogFile");
        initLogFile = (String) databaseProperties.get("initLogFile");

        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibaseInit = new Liquibase(initLogFile, new ClassLoaderResourceAccessor(), database);
            liquibaseInit.update();

            database.setDefaultSchemaName("habits_schema");
            Liquibase liquibaseChanges = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibaseChanges.update();

            dbConnection = connection;
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void applyMigration(Connection connection) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                new JdbcConnection(connection));

        database.setDefaultSchemaName("public");
        Liquibase liquibase = new Liquibase(initLogFile, new ClassLoaderResourceAccessor(), database);
        liquibase.update();

        database.setDefaultSchemaName("habits_schema");
        liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
        liquibase.update();

        dbConnection = connection;
    }
    @Bean
    public static Connection getDbConnection() {
        return dbConnection;
    }

    public static void close() throws SQLException {
        dbConnection.close();
    }
}