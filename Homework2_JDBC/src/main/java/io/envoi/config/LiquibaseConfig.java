package io.envoi.config;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database config file. Creates liquibase tables in public and my tables in habits_schema.
 * */
public class LiquibaseConfig
{
    private static Connection dbConnection;

    private static String changeLogFile;
    private static String initLogFile;
    private static String url;
    private static String username;
    private static String password;

    static {
        Properties properties = new Properties();

        try (FileInputStream fis =
                     new FileInputStream("./src/main/resources/properties/liquibase.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Не найден файл liquibase.properties");
        }

        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        changeLogFile = properties.getProperty("changeLogFile");
        initLogFile = properties.getProperty("initLogFile");

        try {
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

    public static Connection getDbConnection() {
        return dbConnection;
    }

    public static void close() throws SQLException {
        dbConnection.close();
    }
}