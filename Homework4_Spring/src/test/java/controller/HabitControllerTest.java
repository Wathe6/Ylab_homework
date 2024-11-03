package controller;

import io.envoi.controller.HabitController;
import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.model.dto.HabitDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitControllerTest {

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitService habitService;

    @Mock
    private StatisticService statisticService;

    @Mock
    private HabitMapper habitMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get_HabitsExist_ReturnsOkResponse() throws IOException {
        Long accountId = 1L;
        Habit habit = new Habit(1L, accountId, "Exercise", "Morning workout", null);
        List<Habit> habits = Arrays.asList(habit);

        when(habitService.getByAccountId(accountId)).thenReturn(habits);
        when(habitService.getCheckLists(habits, statisticService, habitMapper)).thenReturn(Collections.singletonMap("canBeChecked", List.of()));

        ResponseEntity<?> response = habitController.get(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(habitService, times(1)).getByAccountId(accountId);
    }

    @Test
    void get_HabitsDoNotExist_ReturnsNotFoundResponse() throws IOException {
        Long accountId = 1L;

        when(habitService.getByAccountId(accountId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = habitController.get(accountId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Habits doesn't exist", response.getBody());
    }

    @Test
    void create_ValidHabit_ReturnsOkResponse() {
        HabitDTO habitDTO = new HabitDTO(null, 1L, "Exercise", "Morning workout", Period.ofDays(1));
        Habit habit = new Habit(null, 1L, "Exercise", "Morning workout", null);

        when(habitMapper.toEntity(habitDTO)).thenReturn(habit);
        when(habitService.save(habit)).thenReturn(true);
        when(habitService.get(habit.getAccountId(), habit.getName())).thenReturn(habit);
        when(statisticService.save(any(Statistic.class))).thenReturn(true);

        ResponseEntity<?> response = habitController.create(habitDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Habit saved successfully", response.getBody());
    }

    @Test
    void create_HabitIdIsNotNull_ReturnsBadRequest() {
        HabitDTO habitDTO = new HabitDTO(1L, 1L, "Exercise", "Morning workout", Period.ofDays(1));

        ResponseEntity<?> response = habitController.create(habitDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ID must be null", response.getBody());
    }

    @Test
    void create_RequiredFieldsMissing_ReturnsBadRequest() {
        HabitDTO habitDTO = new HabitDTO(null, null, null, "Morning workout", Period.ofDays(1));

        ResponseEntity<?> response = habitController.create(habitDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Account ID and name are required", response.getBody());
    }

    @Test
    void update_ValidHabit_ReturnsOkResponse() {
        HabitDTO habitDTO = new HabitDTO(null, 1L, "Exercise", "Morning workout", Period.ofDays(1));
        Habit habit = new Habit(1L, 1L, "Exercise", "Morning workout", null);

        when(habitMapper.toEntity(habitDTO)).thenReturn(habit);
        when(habitService.update(habit)).thenReturn(true);

        ResponseEntity<?> response = habitController.update(habitDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Habit updated successfully", response.getBody());
    }

    @Test
    void update_RequiredFieldsMissing_ReturnsBadRequest() {
        HabitDTO habitDTO = new HabitDTO(null, null, null, "Morning workout", Period.ofDays(1));

        ResponseEntity<?> response = habitController.update(habitDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Account ID and name are required", response.getBody());
    }

    @Test
    void delete_HabitExists_ReturnsOkResponse() {
        Long habitId = 1L;
        Habit habit = new Habit(habitId, 1L, "Exercise", "Morning workout", null);

        when(habitService.get(habitId)).thenReturn(habit);
        when(habitService.delete(habitId)).thenReturn(true);

        ResponseEntity<String> response = habitController.delete(habitId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Habit deleted successfully", response.getBody());
    }

    @Test
    void delete_HabitDoesNotExist_ReturnsNotFoundResponse() {
        Long habitId = 1L;

        when(habitService.get(habitId)).thenReturn(null);

        ResponseEntity<String> response = habitController.delete(habitId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Habit not found", response.getBody());
    }

    @Test
    void delete_FailedToDelete_ReturnsInternalServerError() {
        Long habitId = 1L;
        Habit habit = new Habit(habitId, 1L, "Exercise", "Morning workout", null);

        when(habitService.get(habitId)).thenReturn(habit);
        when(habitService.delete(habitId)).thenReturn(false);

        ResponseEntity<String> response = habitController.delete(habitId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete habit", response.getBody());
    }
}