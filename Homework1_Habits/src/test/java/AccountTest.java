import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.service.AccountsService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    private AccountsService accountsService;

    @BeforeEach
    void setup()
    {
        accountsService = new AccountsService();
        accountsService.create("Name1", "mail1@gmail.com", "password1", Roles.PERSON);
        accountsService.create("Name2", "mail2@gmail.com", "password2", Roles.PERSON);
        accountsService.create("Admin", "admin@gmail.com", "adminpass", Roles.ADMIN);
    }

    @Test
    void addAccount()
    {
        Account newAccount = new Account("NewUser", "newuser@gmail.com", "newpassword", Roles.PERSON);
        boolean added = accountsService.add(newAccount);
        assertTrue(added);
        assertNotNull(accountsService.getByEmail("newuser@gmail.com"));
    }

    @Test
    void addDuplicateEmail()
    {
        Account duplicateAccount = new Account("Duplicate", "mail1@gmail.com", "duplicatepass", Roles.PERSON);
        boolean added = accountsService.add(duplicateAccount);
        assertFalse(added);
    }

    @Test
    void getByEmail()
    {
        Account account = accountsService.getByEmail("mail2@gmail.com");
        assertNotNull(account);
        assertEquals("Name2", account.getName());
    }

    @Test
    void getByName()
    {
        Account account = accountsService.getByName("Admin");
        assertNotNull(account);
        assertEquals("admin@gmail.com", account.getEmail());
    }

    @Test
    void updateAccount()
    {
        boolean updated = accountsService.update("mail1@gmail.com", "UpdatedName1", "newpassword1", Roles.ADMIN);
        assertTrue(updated);

        Account updatedAccount = accountsService.getByEmail("mail1@gmail.com");
        assertEquals("UpdatedName1", updatedAccount.getName());
        assertEquals("newpassword1", updatedAccount.getPassword());
        assertEquals(Roles.ADMIN, updatedAccount.getRole());
    }

    @Test
    void updateNonExistentAccount()
    {
        boolean updated = accountsService.update("nonexistent@gmail.com", "NonExistent", "nopassword", Roles.PERSON);
        assertFalse(updated);
    }

    @Test
    void deleteAccount()
    {
        boolean deleted = accountsService.delete("mail1@gmail.com");
        assertTrue(deleted);
        assertNull(accountsService.getByEmail("mail1@gmail.com"));
    }

    @Test
    void deleteNonExistentAccount()
    {
        boolean deleted = accountsService.delete("nonexistent@gmail.com");
        assertFalse(deleted);
    }

    @Test
    void emailExists()
    {
        assertTrue(accountsService.emailExists("mail1@gmail.com"));
        assertFalse(accountsService.emailExists("nonexistent@gmail.com"));
    }

    @Test
    void printAllAccounts()
    {
        accountsService.printAll();
    }
}