package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.model.dto.HabitDTO;
import io.envoi.model.dto.StatisticDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import io.envoi.servlet.StatisticServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;


class StatisticServletTest {

    @Mock
    private StatisticService statisticService;

    @Mock
    private HabitService habitService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private StatisticServlet statisticServlet;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statisticServlet = new StatisticServlet();
        responseWriter = new StringWriter();
    }

    @Test
    @DisplayName("Didn't get statistic for habits.")
    void testStatisticAll_NoHabits() throws Exception {
        Long accountId = 1L;

        when(request.getParameter("accountId")).thenReturn(accountId.toString());
        when(habitService.getByAccountId(accountId)).thenReturn(Collections.emptyList());
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        statisticServlet.statisticALl(request, response);

        verify(response).sendError(HttpServletResponse.SC_CONFLICT, "You have 0 habits.");
    }

    @Test
    @DisplayName("Get all statistic for habits.")
    void testStatisticAll_WithHabits() throws Exception {
        Long accountId = 1L;
        Habit habit = new Habit(1L, "Exercise", "Daily exercise", Period.ofDays(1));
        Statistic statistic = new Statistic(5L, LocalDate.now(), true);
        List<Habit> habits = List.of(habit);
        List<Statistic> statistics = List.of(statistic);
        int streak = 3;
        double percentile = 100.0;

        when(request.getParameter("accountId")).thenReturn(accountId.toString());
        when(habitService.getByAccountId(accountId)).thenReturn(habits);
        when(statisticService.getByHabitId(habit.getId())).thenReturn(statistics);
        when(statisticService.calcStreak(habit.getId())).thenReturn(streak);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        statisticServlet.statisticALl(request, response);

        Map<Long, List<StatisticDTO>> expectedResponse = Map.of(
                habit.getId(),
                List.of(new StatisticDTO(statistic.getDate(), statistic.getMarking(), streak, percentile))
        );
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        assertEquals(jsonResponse, responseWriter.toString().trim());
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("Habit checked.")
    void testCheckHabit_Success() throws Exception {
        HabitDTO habitDTO = new HabitDTO(2L, 2L, "Exercise", "Daily exercise", Period.ofDays(1));
        String json = objectMapper.writeValueAsString(habitDTO);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(statisticService.check(anyLong())).thenReturn(true);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        statisticServlet.checkHabit(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Habit not found when checked.")
    void testCheckHabit_HabitNotFound() throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{}")));
        when(statisticService.check(anyLong())).thenReturn(false);

        statisticServlet.checkHabit(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
    }
}