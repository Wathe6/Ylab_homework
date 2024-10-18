package model;

import io.envoi.model.Habit;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

public class HabitTest {
    private Habit habit;

    @BeforeEach
    void setUp() {
        habit = new Habit("Exercise", "Daily workout", Period.ofDays(1));
    }
    @DisplayName("testHabitCreation")
    @Test
    void testHabitCreation() {
        assertNotNull(habit);
        assertEquals("Exercise", habit.getName());
        assertEquals("Daily workout", habit.getDescription());
        assertEquals(Period.ofDays(1), habit.getPeriod());
        assertEquals(1, habit.getStatistic().size());
        assertTrue(habit.getStatistic().containsKey(LocalDate.now()));
        assertNull(habit.getStatistic().get(LocalDate.now())); // Начальное значение должно быть null
    }
    @DisplayName("testCheckHabit")
    @Test
    void testCheckHabit() {
        habit.check(); // Отмечаем привычку как выполненную

        // Проверяем статус на сегодня
        assertTrue(habit.getStatistic().get(LocalDate.now())); // Значение должно быть true
    }
    @DisplayName("testCanBeCheckedWhenNull")
    @Test
    void testCanBeCheckedWhenNull() {
        assertTrue(habit.canBeChecked()); // Можно отметить, так как значение null
        habit.check();
        assertFalse(habit.canBeChecked()); // Нельзя отметить, так как теперь значение true
    }
    @DisplayName("testCanBeCheckedAfterFillingGaps")
    @Test
    void testCanBeCheckedAfterFillingGaps() {
        // Проставим пропущенные дни
        habit.canBeChecked(); // Заполним пропуски
        habit.check(); // Отметим привычку

        // Создаем новый экземпляр привычки с периодом в 2 дня
        Habit newHabit = new Habit("Reading", "Read daily", Period.ofDays(2));
        newHabit.check(); // Отмечаем привычку

        assertFalse(newHabit.canBeChecked()); // Нельзя отметить, так как сейчас есть запись с true
    }
    @DisplayName("testGetLastStatistic")
    @Test
    void testGetLastStatistic() {
        // Создаем привычку, но не отмечаем её
        assertNull(habit.getLastStatistic()); // Последняя статистика должна быть null

        habit.check(); // Отметим привычку

        assertTrue(habit.getLastStatistic()); // Последняя статистика должна быть true
    }
}