package service;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.service.AccountsService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AccountsServiceTest {

    private AccountsService accountsService;

    @BeforeEach
    void setup()
    {
        accountsService = new AccountsService();
    }
    @DisplayName("testAddAccount")
    @Test
    void testAddAccount()
    {
        Account account = new Account("mail1@gmail.com", "password1", Roles.USER);
        boolean result = accountsService.add(account);

        assertTrue(result, "Account should be added successfully");
        assertNotNull(accountsService.getByEmail("mail1@gmail.com"));
    }
    @DisplayName("testAddDuplicateAccount")
    @Test
    void testAddDuplicateAccount()
    {
        Account account1 = new Account("mail1@gmail.com", "password1", Roles.USER);
        Account account2 = new Account("mail1@gmail.com", "password2", Roles.ADMIN);

        assertTrue(accountsService.add(account1), "First account should be added successfully");
        assertFalse(accountsService.add(account2), "Duplicate account should not be added");
    }
    @DisplayName("testAddNullAccount")
    @Test
    void testAddNullAccount()
    {
        assertFalse(accountsService.add(null), "Null account should not be added");
    }
    @DisplayName("testCreateAccount")
    @Test
    void testCreateAccount()
    {
        boolean result = accountsService.create("mail2@gmail.com", "password2", Roles.ADMIN);

        assertTrue(result, "Account should be created successfully");
        Account createdAccount = accountsService.getByEmail("mail2@gmail.com");
        assertNotNull(createdAccount);
        assertEquals("password2", createdAccount.getPassword());
        assertEquals(Roles.ADMIN, createdAccount.getRole());
    }
    @DisplayName("testCreateDuplicateAccount")
    @Test
    void testCreateDuplicateAccount()
    {
        accountsService.create("mail1@gmail.com", "password1", Roles.USER);
        boolean result = accountsService.create("mail1@gmail.com", "password2", Roles.ADMIN);

        assertFalse(result, "Duplicate email account should not be created");
    }
    @DisplayName("testCreateWithInvalidEmail")
    @Test
    void testCreateWithInvalidEmail()
    {
        boolean result = accountsService.create("", "password1", Roles.USER);
        assertFalse(result, "Account with empty email should not be created");

        result = accountsService.create(null, "password2", Roles.ADMIN);
        assertFalse(result, "Account with null email should not be created");
    }
    @DisplayName("testUpdateAccount")
    @Test
    void testUpdateAccount()
    {
        accountsService.create("mail3@gmail.com", "password3", Roles.USER);
        boolean result = accountsService.update("mail3@gmail.com", "newPassword", Roles.ADMIN);

        assertTrue(result, "Account should be updated successfully");
        Account updatedAccount = accountsService.getByEmail("mail3@gmail.com");
        assertNotNull(updatedAccount);
        assertEquals("newPassword", updatedAccount.getPassword());
        assertEquals(Roles.ADMIN, updatedAccount.getRole());
    }
    @DisplayName("testUpdateNonExistentAccount")
    @Test
    void testUpdateNonExistentAccount()
    {
        boolean result = accountsService.update("nonexistent@gmail.com",  "newPassword", Roles.ADMIN);
        assertFalse(result, "Updating non-existent account should fail");
    }
    @DisplayName("testDeleteAccount")
    @Test
    void testDeleteAccount()
    {
        accountsService.create("mail4@gmail.com", "password4", Roles.USER);
        boolean result = accountsService.delete("mail4@gmail.com");

        assertTrue(result, "Account should be deleted successfully");
        assertNull(accountsService.getByEmail("mail4@gmail.com"), "Deleted account should not exist");
    }
    @DisplayName("testDeleteNonExistentAccount")
    @Test
    void testDeleteNonExistentAccount()
    {
        boolean result = accountsService.delete("nonexistent@gmail.com");
        assertFalse(result, "Deleting non-existent account should fail");
    }
    @DisplayName("testEmailExists")
    @Test
    void testEmailExists()
    {
        accountsService.create("mail5@gmail.com", "password5", Roles.USER);
        assertTrue(accountsService.emailExists("mail5@gmail.com"), "Email should exist after account creation");

        assertFalse(accountsService.emailExists("nonexistent@gmail.com"), "Non-existent email should return false");
    }
}