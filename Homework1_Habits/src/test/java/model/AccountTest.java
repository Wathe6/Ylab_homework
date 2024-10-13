package model;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;
    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";
    private static final Roles ROLE = Roles.USER;

    @BeforeEach
    void setUp()
    {
        account = new Account(EMAIL, PASSWORD, ROLE);
    }

    @Test
    void testConstructorAndGetters()
    {
        assertEquals(EMAIL, account.getEmail());
        assertEquals(PASSWORD, account.getPassword());
        assertEquals(ROLE, account.getRole());
    }

    @Test
    void testSetEmail()
    {
        String newEmail = "new@example.com";
        account.setEmail(newEmail);
        assertEquals(newEmail, account.getEmail());
    }

    @Test
    void testSetPassword()
    {
        String newPassword = "newpassword";
        account.setPassword(newPassword);
        assertEquals(newPassword, account.getPassword());
    }

    @Test
    void testSetRole()
    {
        Roles newRole = Roles.ADMIN;
        account.setRole(newRole);
        assertEquals(newRole, account.getRole());
    }

    @Test
    void testEquals_sameObject()
    {
        assertEquals(account, account);
    }

    @Test
    void testEquals_identicalAccount()
    {
        Account identicalAccount = new Account(EMAIL, PASSWORD, ROLE);
        assertEquals(account, identicalAccount);
    }

    @Test
    void testEquals_differentEmail()
    {
        Account differentEmailAccount = new Account("other@example.com", PASSWORD, ROLE);
        assertNotEquals(account, differentEmailAccount);
    }

    @Test
    void testEquals_nullObject()
    {
        assertNotEquals(account, null);
    }

    @Test
    void testEquals_differentClass()
    {
        assertNotEquals(account, "StringObject");
    }

    @Test
    void testHashCode()
    {
        Account identicalAccount = new Account(EMAIL, PASSWORD, ROLE);
        assertEquals(account.hashCode(), identicalAccount.hashCode());
    }

    @Test
    void testToString()
    {
        String expected = "Account{email='user@example.com', password='password123', role=User}";
        assertEquals(expected, account.toString());
    }

    @Test
    void testSetEmail_nullThrowsException()
    {
        assertThrows(NullPointerException.class, () -> account.setEmail(null));
    }

    @Test
    void testSetPassword_nullThrowsException()
    {
        assertThrows(NullPointerException.class, () -> account.setPassword(null));
    }

    @Test
    void testSetRole_nullThrowsException()
    {
        assertThrows(NullPointerException.class, () -> account.setRole(null));
    }
}

