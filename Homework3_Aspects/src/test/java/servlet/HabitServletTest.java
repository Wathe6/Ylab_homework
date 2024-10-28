package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import io.envoi.servlet.HabitServlet;
import io.envoi.mapper.HabitMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class HabitServletTest {

    private HabitServlet habitServlet;
    private HabitService habitService;
    private StatisticService statisticService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        habitService = mock(HabitService.class);
        statisticService = mock(StatisticService.class);
        objectMapper = new ObjectMapper();
        habitServlet = new HabitServlet();
    }

    @Test
    @DisplayName("doGet all habits for check.")
    public void testDoGet() throws Exception {
        Long accountId = 1L;
        Habit habit = new Habit();
        habit.setId(100L);
        habit.setAccountId(accountId);
        habit.setName("Test Habit");

        when(habitService.getByAccountId(accountId)).thenReturn(List.of(habit));
        when(statisticService.canBeChecked(habit)).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("accountId")).thenReturn(String.valueOf(accountId));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {}

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        habitServlet.doGet(request, response);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    @DisplayName("doPost method to create new habits.")
    public void testDoPost() throws Exception {
        HabitDTO habitDTO = new HabitDTO(1L, 1L, "New Habit", "Description", Period.ofDays(7));

        Habit habit = HabitMapper.INSTANCE.toEntity(habitDTO);

        when(habitService.habitExists(habitDTO.accountId(), habitDTO.name())).thenReturn(false);
        when(habitService.save(habit)).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        byte[] requestBody = objectMapper.writeValueAsBytes(habitDTO);
        InputStream inputStream = new ByteArrayInputStream(requestBody);
        when(request.getInputStream()).thenReturn(inputStream);

        habitServlet.doPost(request, response);

        verify(habitService, times(1)).save(habit);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    @Test
    @DisplayName("doPut to update habits.")
    public void testDoPut() throws Exception {
        HabitDTO habitDTO = new HabitDTO(100L, 100L, "Updated Habit", "Updated Description", Period.ofDays(5));

        Habit habit = new Habit();
        habit.setId(100L);
        habit.setName("Old Habit");

        when(habitService.get(habitDTO.id())).thenReturn(habit);
        when(habitService.update(habit)).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        byte[] requestBody = objectMapper.writeValueAsBytes(habitDTO);
        InputStream inputStream = new ByteArrayInputStream(requestBody);
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getParameter("description")).thenReturn(habitDTO.description());
        when(request.getParameter("period")).thenReturn(String.valueOf(habitDTO.period().getDays()));

        habitServlet.doPut(request, response);

        verify(habitService, times(1)).update(habit);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    @Test
    @DisplayName("doDelete to delete a habit.")
    public void testDoDelete() throws Exception {
        HabitDTO habitDTO = new HabitDTO(100L, 100L, "Habit to delete", "Description", Period.ofDays(7));

        Habit habit = new Habit();
        habit.setId(habitDTO.id());

        when(habitService.get(habitDTO.id())).thenReturn(habit);
        when(habitService.delete(habit.getId())).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        byte[] requestBody = objectMapper.writeValueAsBytes(habitDTO);
        InputStream inputStream = new ByteArrayInputStream(requestBody);
        when(request.getInputStream()).thenReturn(inputStream);

        habitServlet.doDelete(request, response);

        verify(habitService, times(1)).delete(habit.getId());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
}
