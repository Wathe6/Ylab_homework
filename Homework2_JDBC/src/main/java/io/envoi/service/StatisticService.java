package io.envoi.service;

import io.envoi.dao.StatisticDAO;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GetByHabitId, getLastStatistic, check, canBeChecked, calcStreak operations with Statistic. GetAll, get(id), delete, update, isTableEmpty are in BasicService.
 * */
public class StatisticService extends BasicService<Statistic> {
    public StatisticService(StatisticDAO dao) {
        super(dao);
    }

    public List<Statistic> getByHabitId(Long id) {
        return dao.getByField("habit_id", id);
    }

    public Statistic getLastStatistic(Long id) {
        return ((StatisticDAO) dao).getLastDate(id);
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