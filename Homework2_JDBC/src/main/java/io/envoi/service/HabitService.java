package io.envoi.service;

import io.envoi.dao.HabitDAO;
import io.envoi.model.Habit;

import java.util.List;

/**
 * GetByAccountId, habitExists, printAll operations with Habit. GetAll, get(id), delete, update, isTableEmpty are in BasicService.
 * */
public class HabitService extends BasicService<Habit> {

    public HabitService(HabitDAO dao) {
        super(dao);
    }
    public List<Habit> getByAccountId(Long id) {
        return dao.getByField("account_id", id);
    }

    public boolean habitExists(Long accountId, String habitName) {
        return ((HabitDAO) dao).habitExists(accountId, habitName);
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
