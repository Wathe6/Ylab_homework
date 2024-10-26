package io.envoi.service;

import io.envoi.config.LiquibaseConfig;
import io.envoi.dao.AccountDAO;
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
public class AccountServiceTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("psswd200");

    private static AccountService accountService;

    @BeforeAll
    public static void init() throws Exception {
        postgreSQLContainer.start();

        Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());

        LiquibaseConfig.applyMigration(connection);

        AccountDAO accountDAO = new AccountDAO();
        accountDAO.setNewConnection(connection);

        accountService = new AccountService(accountDAO);
    }

    @Test
    @DisplayName("Test saving account")
    public void testSaveAccount() {
        Account newAccount = new Account("test@example.com", "password123", "John Doe", Roles.USER);

        boolean saved = accountService.save(newAccount);
        Assertions.assertThat(saved).isTrue();

        Account fetchedAccount = accountService.getByEmail("test@example.com");
        Assertions.assertThat(fetchedAccount).isNotNull();
        Assertions.assertThat(fetchedAccount.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test updating account")
    public void testUpdateAccount() {
        Account account = new Account("update_test@example.com", "password123", "Jane Doe", Roles.USER);
        boolean saved = accountService.save(account);
        Assertions.assertThat(saved).isTrue();

        account = accountService.getByEmail("update_test@example.com");
        account.setPassword("new_password");
        account.setName("Jane Updated");

        boolean updated = accountService.update(account);
        Assertions.assertThat(updated).isTrue();

        Account updatedAccount = accountService.get(account.getId());
        Assertions.assertThat(updatedAccount.getPassword()).isEqualTo("new_password");
        Assertions.assertThat(updatedAccount.getName()).isEqualTo("Jane Updated");
    }

    @Test
    @DisplayName("Test emailExists")
    public void testEmailExists() {
        String email = "exist_test@example.com";
        Account account = new Account(email, "password456", "Exist Test", Roles.ADMIN);
        boolean saved = accountService.save(account);
        Assertions.assertThat(saved).isTrue();

        boolean exists = accountService.emailExists(email);
        Assertions.assertThat(exists).isTrue();

        boolean nonExists = accountService.emailExists("non_existing@example.com");
        Assertions.assertThat(nonExists).isFalse();
    }

    @Test
    @DisplayName("Test getting account by email")
    public void testGetByEmail() {
        String email = "get_test@example.com";
        Account account = new Account(email, "password789", "Get Test", Roles.USER);
        boolean saved = accountService.save(account);
        Assertions.assertThat(saved).isTrue();

        Account fetchedAccount = accountService.getByEmail(email);
        Assertions.assertThat(fetchedAccount).isNotNull();
        Assertions.assertThat(fetchedAccount.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Test deleting account")
    public void testDeleteAccount() {
        Account accountToDelete = new Account("delete_test@example.com", "password123", "John Delete", Roles.USER);
        boolean saved = accountService.save(accountToDelete);
        Assertions.assertThat(saved).isTrue();

        Account fetchedAccount = accountService.getByEmail("delete_test@example.com");
        boolean deleted = accountService.delete(fetchedAccount.getId());
        Assertions.assertThat(deleted).isTrue();

        Account deletedAccount = accountService.getByEmail("delete_test@example.com");
        Assertions.assertThat(deletedAccount).isNull();
    }

    @AfterAll
    public static void clear() {
        postgreSQLContainer.stop();
    }
}
