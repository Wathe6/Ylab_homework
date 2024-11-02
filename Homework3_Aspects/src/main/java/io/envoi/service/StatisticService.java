package io.envoi.service;

import io.envoi.dao.StatisticDAO;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.model.dto.StatisticDTO;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GetByHabitId, getLastStatistic, check, canBeChecked, calcStreak operations with Statistic. GetAll, get(id), delete, update, isTableEmpty are in BasicService.
 * */
public class StatisticService extends BasicService<Statistic, StatisticDTO> {
    public StatisticService(StatisticDAO dao) {
        super(dao);
    }

    public List<Statistic> getByHabitId(Long id) {
        return dao.getByField("habit_id", id);
    }

    public Statistic getLastStatistic(Long id) {
        return ((StatisticDAO) dao).getLastDate(id);
    }

    public Map<Long, List<StatisticDTO>> getALl(List<Habit> habits) {
        Map<Long, List<StatisticDTO>> result = new LinkedHashMap<>();

        for (Habit habit : habits) {
            Long habitId = habit.getId();
            List<Statistic> statistics = getByHabitId(habitId);
            int streak = calcStreak(habitId);
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

            result.put(habitId, statisticDTOs);
        }

        return result;
    }

    public boolean check(Long id) {
        Statistic statistic = getLastStatistic(id);
        statistic.setMarking(true);
        return dao.update(statistic);
    }

    public boolean canBeChecked(Habit habit) {
        Long id = habit.getId();

        LocalDate today = LocalDate.now();
        Statistic statistic = getLastStatistic(id);

        LocalDate lastDate = statistic.getDate();
        Period period = habit.getPeriod();

        // Доставляем пропущенные записи до сегодняшнего дня
        while (lastDate.isBefore(today)) {
            lastDate = lastDate.plus(period);
            if (lastDate.isBefore(today)) {
                dao.save(new Statistic(id, lastDate, false)); // Пропущенные дни
            } else if (lastDate.equals(today)) {
                dao.save(new Statistic(id, lastDate, null)); // Сегодняшний день
            }
        }

        return getLastStatistic(id).getMarking() == null;
    }

    public int calcStreak(Long habitId) {
        int streak = 0;

        List<Boolean> statuses = new ArrayList<>(getByHabitId(habitId).stream().map(Statistic::getMarking).toList());
        Collections.reverse(statuses);

        for (Boolean status : statuses) {
            if (Boolean.TRUE.equals(status)) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}