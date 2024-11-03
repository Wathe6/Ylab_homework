package controller;

import io.envoi.controller.StatisticController;
import io.envoi.dao.StatisticDAO;
import io.envoi.mapper.HabitMapper;
import io.envoi.mapper.StatisticMapper;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.model.dto.StatisticDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatisticControllerTest {

    @InjectMocks
    private StatisticController statisticController;

    @Mock
    private StatisticService statisticService;

    @Mock
    private HabitService habitService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticController).build();
    }

    @Test
    @DisplayName("Check habit successful")
    void testCheck_Success() throws Exception {
        Long habitId = 1L;
        Habit habit = new Habit();
        habit.setId(habitId);

        when(habitService.get(habitId)).thenReturn(habit);
        when(statisticService.check(habitId)).thenReturn(true);

        mockMvc.perform(get("/api/statistics/{habitId}/check", habitId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Habit checked successfully"));
    }

    @Test
    @DisplayName("Check habit failed - Habit not found")
    void testCheck_HabitNotFound() throws Exception {
        Long habitId = 1L;

        when(habitService.get(habitId)).thenReturn(null);

        mockMvc.perform(get("/api/statistics/{habitId}/check", habitId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Habit not found"));
    }

    @Test
    @DisplayName("Check habit failed - Error during check")
    void testCheck_ErrorDuringCheck() throws Exception {
        Long habitId = 1L;
        Habit habit = new Habit();
        habit.setId(habitId);

        when(habitService.get(habitId)).thenReturn(habit);
        when(statisticService.check(habitId)).thenReturn(false);

        mockMvc.perform(get("/api/statistics/{habitId}/check", habitId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Failed to check habit"));
    }

    @Test
    @DisplayName("Get all statistics successful")
    void testGetAll_Success() throws Exception {
        Long accountId = 1L;
        Habit habit = new Habit();
        habit.setId(1L);
        List<Habit> habits = Collections.singletonList(habit);
        StatisticDTO statisticDTO = StatisticMapper.INSTANCE.toDTO(new Statistic());
        Map<Long, List<StatisticDTO>> statisticsMap = Collections.singletonMap(habit.getId(), Collections.singletonList(statisticDTO));

        when(habitService.getByAccountId(accountId)).thenReturn(habits);
        when(statisticService.getALl(habits)).thenReturn(statisticsMap);

        mockMvc.perform(get("/api/statistics/{accountId}/all", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{\"1\":[{}]}"));
    }

    @Test
    @DisplayName("Get all statistics failed - Habits don't exist")
    void testGetAll_HabitsNotExist() throws Exception {
        Long accountId = 1L;

        when(habitService.getByAccountId(accountId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/statistics/{accountId}/all", accountId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Habits doesn't exist"));
    }

    @Test
    @DisplayName("Get all statistics failed - Error sorting habits")
    void testGetAll_ErrorSorting() throws Exception {
        Long accountId = 1L;
        Habit habit = new Habit();
        habit.setId(1L);
        List<Habit> habits = Collections.singletonList(habit);

        when(habitService.getByAccountId(accountId)).thenReturn(habits);
        when(statisticService.getALl(habits)).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/statistics/{accountId}/all", accountId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Error sorting habits"));
    }
}