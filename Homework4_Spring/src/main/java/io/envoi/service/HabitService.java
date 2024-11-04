package io.envoi.service;

import io.envoi.dao.HabitDAO;
import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.dto.HabitDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GetByAccountId, habitExists, printAll operations with Habit. GetAll, get(id), delete, update, isTableEmpty are in BasicService.
 * */
@Service
public class HabitService extends BasicService<Habit, HabitDTO> {
    public HabitService(HabitDAO dao) {
        super(dao);
    }
    public List<Habit> getByAccountId(Long id) {
        return dao.getByField("account_id", id);
    }

    public Habit get(Long accountId, String habitName) {
        return ((HabitDAO) dao).get(accountId, habitName);
    }

    public Map<String, List<HabitDTO>> getCheckLists(List<Habit> habits, StatisticService statisticService, HabitMapper habitMapper) {
        Map<String, List<HabitDTO>> result = new HashMap<>();
        List<HabitDTO> canBeChecked = new ArrayList<>();
        List<HabitDTO> cannotBeChecked = new ArrayList<>();

        habits.forEach(habit -> {
            if (statisticService.canBeChecked(habit)) {
                canBeChecked.add(habitMapper.toDTO(habit));
            } else {
                cannotBeChecked.add(habitMapper.toDTO(habit));
            }
        });

        result.put("canBeChecked", canBeChecked);
        result.put("cannotBeChecked", cannotBeChecked);

        return result;
    }

    public List<Habit> printAll(Long accountId) {
        List<Habit> habits = getByAccountId(accountId);

        if (habits.isEmpty()) {
            System.out.println("У вас нет привычек!");
            return null;
        }

        System.out.println("Список привычек для изменения:\n0. Выход.");
        int i;
        for (i = 0; i < habits.size(); i++) {
            System.out.println(i + ". " + habits.get(i).getName());
        }

        return habits;
    }
}