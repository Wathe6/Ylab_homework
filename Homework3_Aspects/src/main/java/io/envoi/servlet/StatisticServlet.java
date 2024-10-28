package io.envoi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.annotations.Loggable;
import io.envoi.dao.HabitDAO;
import io.envoi.dao.StatisticDAO;
import io.envoi.mapper.HabitMapper;
import io.envoi.mapper.StatisticMapper;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.model.dto.HabitDTO;
import io.envoi.model.dto.StatisticDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Get statistic and check habits.
 * */
@Loggable
@WebServlet(name="StatisticServlet", urlPatterns = "/api/statistic/*")
public class StatisticServlet extends HttpServlet {
    private final StatisticService statisticService = new StatisticService(new StatisticDAO());
    private final HabitService habitService = new HabitService(new HabitDAO());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HabitMapper habitMapper = HabitMapper.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo();

        if (action == null)
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        switch (action)
        {
            case "/all" -> statisticALl(req, resp);
            case "/check" -> checkHabit(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Action not found");
        }

    }

    public void checkHabit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HabitDTO habitDTO = objectMapper.readValue(req.getReader(), HabitDTO.class);
        Habit habit = habitMapper.toEntity(habitDTO);

        if (habit == null)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
            return;
        }

        if (statisticService.check(habit.getId()))
        {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else
        {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit wasn't checked.");
        }
    }

    public void statisticALl(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long accountId = Long.parseLong(req.getParameter("accountId"));

        List<Habit> habits = habitService.getByAccountId(accountId);

        if(CollectionUtils.isEmpty(habits)) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "You have 0 habits.");
            return;
        }

        Map<Long, List<StatisticDTO>> habitStatisticsMap = new LinkedHashMap<>();

        for (Habit habit : habits) {
            Long habitId = habit.getId();
            List<Statistic> statistics = statisticService.getByHabitId(habitId);
            int streak = statisticService.calcStreak(habitId);
            long totalDays = statistics.size();
            long completedDays = statistics.stream().filter(stat -> Boolean.TRUE.equals(stat.getMarking())).count();
            double percentile = totalDays > 0 ? ((double) completedDays / totalDays) * 100 : 0.0;

            List<StatisticDTO> statisticDTOs = statistics.stream()
                    .map(stat -> new StatisticDTO(
                            stat.getDate(),
                            stat.getMarking(),
                            streak,
                            percentile))
                    .collect(Collectors.toList());

            habitStatisticsMap.put(habitId, statisticDTOs);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(resp.getWriter(), habitStatisticsMap);
    }
}