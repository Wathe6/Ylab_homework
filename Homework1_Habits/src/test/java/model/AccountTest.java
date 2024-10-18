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
    @DisplayName("testConstructorAndGetters")
    @Test
    void testConstructorAndGetters()
    {
        assertEquals(EMAIL, account.getEmail());
        assertEquals(PASSWORD, account.getPassword());
        assertEquals(ROLE, account.getRole());
    }
    @DisplayName("testSetEmail")
    @Test
    void testSetEmail()
    {
        String newEmail = "new@example.com";
        account.setEmail(newEmail);
        assertEquals(newEmail, account.getEmail());
    }
    @DisplayName("testSetPassword")
    @Test
    void testSetPassword()
    {
        String newPassword = "newpassword";
        account.setPassword(newPassword);
        assertEquals(newPassword, account.getPassword());
    }
    @DisplayName("testSetRole")
    @Test
    void testSetRole()
    {
        Roles newRole = Roles.ADMIN;
        account.setRole(newRole);
        assertEquals(newRole, account.getRole());
    }
    @DisplayName("testEquals_sameObject")
    @Test
    void testEquals_sameObject()
    {
        assertEquals(account, account);
    }
    @DisplayName("testEquals_identicalAccount")
    @Test
    void testEquals_identicalAccount()
    {
        Account identicalAccount = new Account(EMAIL, PASSWORD, ROLE);
        assertEquals(account, identicalAccount);
    }
    @DisplayName("testEquals_differentEmail")
    @Test
    void testEquals_differentEmail()
    {
        Account differentEmailAccount = new Account("other@example.com", PASSWORD, ROLE);
        assertNotEquals(account, differentEmailAccount);
    }
    @DisplayName("testEquals_nullObject")
    @Test
    void testEquals_nullObject()
    {
        assertNotEquals(account, null);
    }
    @DisplayName("testEquals_differentClass")
    @Test
    void testEquals_differentClass()
    {
        assertNotEquals(account, "StringObject");
    }
    @DisplayName("testHashCode")
    @Test
    void testHashCode()
    {
        Account identicalAccount = new Account(EMAIL, PASSWORD, ROLE);
        assertEquals(account.hashCode(), identicalAccount.hashCode());
    }
    @DisplayName("testToString")
    @Test
    void testToString()
    {
        String expected = "Account{email='user@example.com', password='password123', role=User}";
        assertEquals(expected, account.toString());
    }
    @DisplayName("testSetEmail_nullThrowsException")
    @Test
    void testSetEmail_nullThrowsException()
    {
        assertThrows(NullPointerException.class, () -> account.setEmail(null));
    }
    @DisplayName("testSetPassword_nullThrowsException")
    @Test
    void testSetPassword_nullThrowsException()
    {
        assertThrows(NullPointerException.class, () -> account.setPassword(null));
    }
    @DisplayName("testSetRole_nullThrowsException")
    @Test
    void testSetRole_nullThrowsException()
    {
        assertThrows(NullPointerException.class, () -> account.setRole(null));
    }
}

