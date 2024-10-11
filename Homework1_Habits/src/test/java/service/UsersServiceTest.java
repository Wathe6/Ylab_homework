package service;

import io.envoi.model.Habit;
import io.envoi.model.User;
import io.envoi.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class UsersServiceTest {

    private UsersService usersService;

    @BeforeEach
    public void setUp() {
        usersService = new UsersService();
    }

    @Test
    public void testAddUserSuccess()
    {
        User user = new User("test@example.com", "Test User", new HashMap<>());
        boolean result = usersService.add(user);

        assertTrue(result, "User should be added successfully");
        assertEquals(user, usersService.getByEmail("test@example.com"));
    }

    @Test
    public void testAddUserFail_NullUser()
    {
        boolean result = usersService.add(null);

        assertFalse(result, "Adding null user should fail");
    }

    @Test
    public void testGetByEmail_UserExists()
    {
        User user = new User("test@example.com", "Test User", new HashMap<>());
        usersService.add(user);

        User result = usersService.getByEmail("test@example.com");

        assertNotNull(result, "User should be found");
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
    }

    @Test
    public void testGetByEmail_UserDoesNotExist()
    {
        User result = usersService.getByEmail("nonexistent@example.com");

        assertNull(result, "User should not be found");
    }

    @Test
    public void testGetHabits_UserExists()
    {
        User user = new User("test@example.com", "Test User", new HashMap<>());
        usersService.add(user);

        Map<String, Habit> habits = usersService.getHabits("test@example.com");

        assertNotNull(habits, "Habits map should not be null");
        assertTrue(habits.isEmpty(), "User should have no habits initially");
    }

    @Test
    public void testGetHabits_UserDoesNotExist()
    {
        Map<String, Habit> habits = usersService.getHabits("nonexistent@example.com");

        assertNull(habits, "Habits map should be null for nonexistent user");
    }

    @Test
    public void testCreateUserSuccess()
    {
        boolean result = usersService.create("test@example.com", "Test User");

        assertTrue(result, "User should be created successfully");
        User user = usersService.getByEmail("test@example.com");
        assertNotNull(user, "Created user should be retrievable");
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
    }

    @Test
    public void testCreateUserFail_EmptyEmail()
    {
        boolean result = usersService.create("", "Test User");

        assertFalse(result, "User creation should fail for empty email");
    }

    @Test
    public void testCreateUserFail_NullEmail()
    {
        boolean result = usersService.create(null, "Test User");

        assertFalse(result, "User creation should fail for null email");
    }

    @Test
    public void testCreateUserFail_EmailExists()
    {
        usersService.create("test@example.com", "Test User");
        boolean result = usersService.create("test@example.com", "Test User 2");

        assertFalse(result, "User creation should fail for existing email");
    }

    @Test
    public void testDeleteUserSuccess()
    {
        usersService.create("test@example.com", "Test User");

        boolean result = usersService.delete("test@example.com");

        assertTrue(result, "User should be deleted successfully");
        assertNull(usersService.getByEmail("test@example.com"), "Deleted user should not be retrievable");
    }

    @Test
    public void testDeleteUserFail_UserDoesNotExist()
    {
        boolean result = usersService.delete("nonexistent@example.com");

        assertFalse(result, "Deleting nonexistent user should fail");
    }

    @Test
    public void testEmailExists_UserExists()
    {
        usersService.create("test@example.com", "Test User");

        assertTrue(usersService.emailExists("test@example.com"), "Email should exist");
    }

    @Test
    public void testEmailExists_UserDoesNotExist()
    {
        assertFalse(usersService.emailExists("nonexistent@example.com"), "Email should not exist");
    }
}

