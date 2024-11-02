package io.envoi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.annotations.Loggable;
import io.envoi.dao.HabitDAO;
import io.envoi.dao.StatisticDAO;
import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import io.envoi.model.dto.StatisticDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Get statistic and check habits.
 * */
@Loggable
@WebServlet(name="StatisticServlet", urlPatterns = "/api/statistic/*")
public class StatisticServlet extends HttpServlet {
    private final StatisticService statisticService;
    private final HabitService habitService;
    private final ObjectMapper objectMapper;
    private final HabitMapper habitMapper;

    public StatisticServlet() {
        this.statisticService = new StatisticService(new StatisticDAO());
        this.habitService = new HabitService(new HabitDAO());
        this.objectMapper = new ObjectMapper();
        this.habitMapper = HabitMapper.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo();

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        switch (action) {
            case "/all" -> statisticALl(req, resp);
            case "/check" -> checkHabit(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Action not found");
        }

    }

    public void checkHabit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HabitDTO habitDTO = objectMapper.readValue(req.getReader(), HabitDTO.class);
        Habit habit = habitMapper.toEntity(habitDTO);

        if (habit == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
            return;
        }

        if (statisticService.check(habit.getId())) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
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

        Map<Long, List<StatisticDTO>> habitStatisticsMap = statisticService.getALl(habits);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(resp.getWriter(), habitStatisticsMap);
    }
}