package model;

import io.envoi.model.Habit;
import io.envoi.model.User;
import org.junit.jupiter.api.*;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class UserTest
{
    private User user;
    private Habit habit1;
    private Habit habit2;

    @BeforeEach
    void setUp()
    {
        habit1 = new Habit("Exercise", "Daily exercise", Period.ofDays(1));
        habit2 = new Habit("Read", "Read books", Period.ofDays(2));

        Map<String, Habit> habits = new HashMap<>();
        user = new User("test@example.com", "John Doe", habits);
    }
    @DisplayName("testAddHabit")
    @Test
    void testAddHabit()
    {
        assertTrue(user.addHabit(habit1), "Should return true when adding a new habit");
        assertTrue(user.containsHabit(habit1), "Habit should be present after being added");

        assertFalse(user.addHabit(habit1), "Should return false when adding an existing habit");
    }
    @DisplayName("testRemoveHabit")
    @Test
    void testRemoveHabit()
    {
        user.addHabit(habit1);
        assertTrue(user.removeHabit(habit1), "Should return true when habit is successfully removed");
        assertFalse(user.containsHabit(habit1), "Habit should no longer be present after removal");

        assertFalse(user.removeHabit(habit2), "Should return false when trying to remove a non-existent habit");
    }
    @DisplayName("testFindHabit")
    @Test
    void testFindHabit()
    {
        user.addHabit(habit1);
        assertEquals(habit1, user.findHabit("Exercise"), "Should find the correct habit by name");

        assertNull(user.findHabit("NonExistent"), "Should return null for a non-existent habit");
    }
    @DisplayName("testGetHabitsToMark")
    @Test
    void testGetHabitsToMark()
    {
        user.addHabit(habit1);
        user.addHabit(habit2);

        assertEquals(2, user.getHabitsToMark().size(), "Should return all habits that can be marked");

        habit1.check();
        assertEquals(1, user.getHabitsToMark().size(), "Should return only habits that can still be marked");
    }
    @DisplayName("testSettersAndGetters")
    @Test
    void testSettersAndGetters()
    {
        assertEquals("test@example.com", user.getEmail(), "Email should be correct");
        assertEquals("John Doe", user.getName(), "Name should be correct");

        user.setEmail("newemail@example.com");
        user.setName("Jane Doe");

        assertEquals("newemail@example.com", user.getEmail(), "Email should be updated correctly");
        assertEquals("Jane Doe", user.getName(), "Name should be updated correctly");
    }
    @DisplayName("testEqualsAndHashCode")
    @Test
    void testEqualsAndHashCode()
    {
        User sameUser = new User("test@example.com", "John Doe", new HashMap<>());
        User differentUser = new User("different@example.com", "John Doe", new HashMap<>());

        assertEquals(user, sameUser, "Users with the same email should be equal");
        assertNotEquals(user, differentUser, "Users with different emails should not be equal");

        assertEquals(user.hashCode(), sameUser.hashCode(), "Hashcodes should be equal for users with the same email");
        assertNotEquals(user.hashCode(), differentUser.hashCode(), "Hashcodes should be different for users with different emails");
    }
}
