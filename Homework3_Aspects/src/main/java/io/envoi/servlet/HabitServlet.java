package io.envoi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.annotations.Loggable;
import io.envoi.dao.HabitDAO;
import io.envoi.dao.StatisticDAO;
import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.time.Period;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Create, update, getAll or delete habits.
 * */
@Loggable
@WebServlet(name="HabitServlet", urlPatterns = "/api/habit/*")
public class HabitServlet extends HttpServlet {
    private final HabitService habitService;
    private final StatisticService statisticService;
    private final ObjectMapper objectMapper;
    private final HabitMapper habitMapper;

    public HabitServlet() {
        this.habitService = new HabitService(new HabitDAO());
        this.statisticService = new StatisticService(new StatisticDAO());
        this.objectMapper = new ObjectMapper();
        this.habitMapper = HabitMapper.INSTANCE;
    }

    /**
     * Get the list of checked and unchecked habits.
     * */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long accountId = Long.parseLong(req.getParameter("accountId"));

        List<Habit> habits = habitService.getByAccountId(accountId);

        if(CollectionUtils.isEmpty(habits)) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "You have 0 habits.");
            return;
        }

        Map<String, List<HabitDTO>> result = habitService.getCheckLists(habits, statisticService, habitMapper);

        String jsonResponse = objectMapper.writeValueAsString(result);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonResponse);
    }

    /**
     * Create a new habit.
     * */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HabitDTO habitDTO = objectMapper.readValue(req.getInputStream(), HabitDTO.class);
        Long accountId = habitDTO.accountId();

        if (habitService.habitExists(accountId, habitDTO.name())) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit already exists");
            return;
        }

        Habit habit = habitMapper.toEntity(habitDTO);
        if (habitService.save(habit)) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit wasn't saved.");
        }
    }

    /**
     * Change existing habit.
     * */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HabitDTO habitDTO = objectMapper.readValue(req.getInputStream(), HabitDTO.class);
        Habit habit = habitService.get(habitDTO.id());

        if (habit == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
            return;
        }

        habit.setDescription(req.getParameter("description"));
        habit.setPeriod(Period.ofDays(Integer.parseInt(req.getParameter("period"))));

        if(habitService.update(habit)) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit wasn't updated.");
        }
    }

    /**
     * Delete a habit.
     * */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HabitDTO habitDTO = objectMapper.readValue(req.getInputStream(), HabitDTO.class);
        Habit habit = habitService.get(habitDTO.id());

        if (!habitService.delete(habit.getId())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found or couldn't be deleted");
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}