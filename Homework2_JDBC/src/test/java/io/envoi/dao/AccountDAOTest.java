package io.envoi.dao;

import io.envoi.config.LiquibaseConfig;
import io.envoi.enums.Roles;
import io.envoi.model.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

@Testcontainers
public class AccountDAOTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("psswd200");

    private static AccountDAO accountDAO;

    @BeforeAll
    public static void init() throws Exception {
        postgreSQLContainer.start();

        Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());

        LiquibaseConfig.applyMigration(connection);

        accountDAO = new AccountDAO();
        accountDAO.setNewConnection(connection);
    }

    @Test
    @DisplayName("Test saving")
    public void testSaveAccount() {
        Account newAccount = new Account("test@example.com", "password123", "John Doe", Roles.USER);

        boolean saved = accountDAO.save(newAccount);
        Assertions.assertThat(saved).isTrue();

        newAccount = accountDAO.getByField("email", "test@example.com").get(0);
        Assertions.assertThat(newAccount.getId()).isNotNull();

        boolean emailExists = accountDAO.emailExists(newAccount.getEmail());
        Assertions.assertThat(emailExists).isTrue();
    }

    @Test
    @DisplayName("Test update")
    public void testUpdateAccount() {
        Account account = new Account("update_test@example.com", "password123", "Jane Doe", Roles.USER);
        boolean saved = accountDAO.save(account);
        Assertions.assertThat(saved).isTrue();

        account = accountDAO.getByField("email", "update_test@example.com").get(0);
        account.setPassword("new_password");
        account.setName("Jane Updated");

        boolean updated = accountDAO.update(account);
        Assertions.assertThat(updated).isTrue();

        Account updatedAccount = accountDAO.get(account.getId());
        Assertions.assertThat(updatedAccount.getPassword()).isEqualTo("new_password");
        Assertions.assertThat(updatedAccount.getName()).isEqualTo("Jane Updated");
    }

    @Test
    @DisplayName("Test emailExists")
    public void testEmailExists() {
        String email = "exist_test@example.com";
        Account account = new Account(email, "password456", "Exist Test", Roles.ADMIN);
        accountDAO.save(account);

        boolean exists = accountDAO.emailExists(email);

        Assertions.assertThat(exists).isTrue();

        boolean nonExists = accountDAO.emailExists("non_existing@example.com");
        Assertions.assertThat(nonExists).isFalse();
    }

    @Test
    @DisplayName("Test delete")
    public void testDeleteAccount() {
        Account accountToDelete = new Account("delete_test@example.com", "password123", "John Delete", Roles.USER);
        boolean saved = accountDAO.save(accountToDelete);
        Assertions.assertThat(saved).isTrue();

        accountToDelete = accountDAO.getByField("email", "delete_test@example.com").get(0);
        boolean deleted = accountDAO.delete(accountToDelete.getId());
        Assertions.assertThat(deleted).isTrue();

        Account fetchedAccount = accountDAO.get(accountToDelete.getId());
        Assertions.assertThat(fetchedAccount).isNull();
    }

    @AfterAll
    public static void clear() {
        postgreSQLContainer.stop();
    }
}