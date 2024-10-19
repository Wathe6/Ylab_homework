package io.envoi.config;

import liquibase.Scope;
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

public class LiquibaseConfig
{
    private static Connection dbConnection;

    static {
        Properties properties = new Properties();

        try (FileInputStream fis =
                     new FileInputStream("./src/main/resources/properties/liquibase.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Не найден файл liquibase.properties");
        }

        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String changeLogFile = properties.getProperty("changeLogFile");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(connection));
            database.setDefaultSchemaName("habits_schema");
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();

            dbConnection = connection;
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }
    }
    public static Connection getDbConnection() {
        return dbConnection;
    }
    public static void close() throws SQLException {
        dbConnection.close();
    }
}