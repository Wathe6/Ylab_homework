package io.envoi.menu;

import io.envoi.model.Account;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static io.envoi.in.Input.inputInt;
import static io.envoi.in.Input.inputLine;

public class HabitsMenu {
    private HabitService habitService;
    private StatisticService statisticService;
    private Account myAccount;

    public HabitsMenu(HabitService habitService, StatisticService statisticService) {
        this.habitService = habitService;
        this.statisticService = statisticService;
    }

    public void setMyAccount(Account account) {
        this.myAccount = account;
    }

    public void habitsMenu() {
        while (true) {
            System.out.println("""
                    Введите цифру, чтобы продолжить:
                    0. Выход.
                    1. Отметить привычки.
                    2. Добавить привычку.
                    3. Изменить привычку.
                    4. Удалить привычку.""");

            int input = inputInt("Введите номер операции: ", 0, 4);

            switch (input) {
                case 0 -> {
                    return;
                }
                case 1 -> habitsCheckMenu();
                case 2 -> habitsAddMenu();
                case 3 -> habitsChangeMenu();
                case 4 -> habitsDeleteMenu();
                default -> System.out.println("Вы ввели некорректный номер!");
            }
        }
    }

    public void habitsCheckMenu() {
        List<Habit> habits = habitService.getByAccountId(myAccount.getId());

        if (habits.isEmpty()) {
            System.out.println("У вас нет привычек!");
            return;
        }

        List<Habit> canBeChecked = new ArrayList<>();
        List<Habit> cannotBeChecked = new ArrayList<>();
        //Разделяем привычки
        habits.forEach((habit) -> {
            if (statisticService.canBeChecked(habit)) {
                canBeChecked.add(habit);
            } else {
                cannotBeChecked.add(habit);
            }
        });

        do {
            System.out.println("Список привычек, которые можно отметить:\n0. Выход.");
            int i;
            for (i = 0; i < canBeChecked.size(); i++) {
                System.out.println(i + ". " + canBeChecked.get(i).getName());
            }
            System.out.println("\nСписок привычек, которые нельзя отметить:");
            for (Habit habit : cannotBeChecked) {
                LocalDate lastDate = statisticService.getLastStatistic(habit.getId()).getDate();
                System.out.println(habit.getName() + " - " + lastDate.plus(habit.getPeriod()));
            }

            while (true) {
                int input = inputInt("\nВведите номер привычки:", 0, i);

                if (input == 0) {
                    return;
                }

                if (input > i || input < 0) {
                    System.out.println("Привычка с таким номером не найдена. Попробуйте снова.");
                    continue;
                }

                statisticService.check(canBeChecked.get(input - 1).getId());
                cannotBeChecked.add(canBeChecked.get(input - 1));
                System.out.println("Привычка " + canBeChecked.get(input - 1).getName() + " успешно отмечена!");
                canBeChecked.remove(input - 1);
                break;
            }
        } while (true);
    }

    public void habitsAddMenu() {
        String habitName, description;
        Period period;
        while (true) {
            habitName = inputLine("Введите название привычки: ");

            if (habitService.habitExists(myAccount.getId(), habitName)) {
                System.out.println("Привычка с таким именем уже существует!");
                continue;
            }

            description = inputLine("Введите описание: ");

            int input = inputInt("Введите период в днях: ", 1, 30);
            period = Period.ofDays(input);

            if (habitService.save(new Habit(myAccount.getId(), habitName, description, period))) {
                System.out.println("Привычка успешно добавлена!");

                Habit lastHabit = habitService.getAll().stream()
                        .filter(h -> h.getAccountId().equals(myAccount.getId()))
                        .reduce((first, second) -> second) // Возвращаем последнюю привычку
                        .orElse(null);

                Statistic statistic = new Statistic(lastHabit.getId(), LocalDate.now(), null);
                statisticService.save(statistic);
                break;
            } else {
                System.out.println("Привычка не было добавлена, попробуйте ещё раз.");
            }
        }
    }

    public void habitsChangeMenu() {
        List<Habit> habits = habitService.printAll(myAccount.getId());
        int i = habits.size();

        while (true) {
            String description;
            Period period;
            int input = inputInt("\nВведите номер привычки:", 0, i);

            if (input == 0) {
                return;
            }

            if (input > i || input < 0) {
                System.out.println("Привычка с таким номером не найдена. Попробуйте снова.");
                continue;
            }

            Habit habit = habits.get(i);
            description = inputLine("Введите описание: ");
            habit.setDescription(description);

            int newPeriod = inputInt("Введите период в днях: ", 1, 30);
            period = Period.ofDays(newPeriod);
            habit.setPeriod(period);

            if(habitService.update(habit)) {
                System.out.println("Привычка успешно изменена!");
                habits.set(i, habit);
            } else {
                System.out.println("Привычка не было изменена, попробуйте ещё раз.");
            }
        }
    }

    public void habitsDeleteMenu() {
        List<Habit> habits = habitService.printAll(myAccount.getId());
        int i = habits.size();

        while (true) {
            int input = inputInt("\nВведите номер привычки:", 0, i);

            if (input == 0) {
                return;
            }

            if (input > i || input < 0) {
                System.out.println("Привычка с таким номером не найдена. Попробуйте снова.");
                continue;
            }

            Habit habit = habits.get(i);

            if(habitService.delete(habit)) {
                System.out.println("Привычка успешно удалена!");
                habits.remove(i);
            } else {
                System.out.println("Привычка не было удалена, попробуйте ещё раз.");
            }
        }
    }
}
