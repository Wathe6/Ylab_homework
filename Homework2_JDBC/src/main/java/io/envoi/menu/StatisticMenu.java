package io.envoi.menu;

import io.envoi.model.Account;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;

import java.time.LocalDate;
import java.util.List;

import static io.envoi.in.Input.*;

public class StatisticMenu {
    private HabitService habitService;
    private StatisticService statisticService;
    private Account myAccount;

    public StatisticMenu(HabitService habitService, StatisticService statisticService) {
        this.habitService = habitService;
        this.statisticService = statisticService;
    }

    public void setMyAccount(Account account) {
        this.myAccount = account;
    }

    public void statisticMenu() {
        while (true) {
            System.out.println("""
                    Введите цифру, чтобы продолжить:
                    0. Выход.
                    1. Список всех привычек.
                    2. Список всех привычек по статусу.
                    3. Список всех привычек в промежутке времени.
                    4. Серии выполнения привычек (streak).
                    5. Статистика по привычке.
                    6. Процент выполнения.""");

            int input = inputInt("Введите номер операции: ", 0, 6);

            switch (input) {
                case 0 -> {return;}
                case 1 -> statisticAll();
                case 2 -> statisticByStatus();
                case 3 -> statisticByDate();
                case 4 -> statisticStreaks();
                case 5 -> statisticByName();
                case 6 -> statisticPercentile();
                default -> System.out.println("Вы ввели некорректный номер!");
            }
        }
    }

    public void statisticAll() {
        System.out.println("Список всех привычек:");
        habitService.getByAccountId(myAccount.getId()).forEach((habit) -> {
            System.out.format("Привычка: %s%n", habit.getName());
            statisticService.getByHabitId(habit.getId()).forEach((statistic) -> {
                String statusText = statistic.getMarking() == null ? "не отмечена" : statistic.getMarking() ? "выполнена" : "не выполнена";
                System.out.format("  Дата: %s - Статус: %s%n", statistic.getDate() , statusText);
            });
            System.out.println();
        });
    }

    public void statisticByStatus() {
        System.out.println("""
        Выберите статус привычек для отображения:
        1. Выполнена.
        2. Не выполнена.
        3. Не отмечена.
        """);

        int input = inputInt("Введите номер статуса: ", 1, 3);
        String statusText = switch (input) {
            case 1 -> "выполнена";
            case 2 -> "не выполнена";
            case 3 -> "не отмечена";
            default -> "";
        };

        System.out.format("Привычки со статусом %s:%n", statusText);

        habitService.getByAccountId(myAccount.getId()).forEach((habit) -> {
            statisticService.getByHabitId(habit.getId()).forEach((statistic) -> {
                Boolean status = statistic.getMarking();

                boolean matches = switch (input) {
                    case 1 -> Boolean.TRUE.equals(status);
                    case 2 -> Boolean.FALSE.equals(status);
                    case 3 -> status == null;
                    default -> false;
                };

                if (matches) {
                    System.out.format("Привычка: %s - Дата: %s - Статус: %s%n", habit.getName(), statistic.getDate().toString(), statusText);
                }
            });
        });
    }

    public void statisticByDate() {
        LocalDate startDate = inputDate("Введите начальную дату (гггг-мм-дд): ");
        LocalDate endDate = inputDate("Введите конечную дату (гггг-мм-дд): ");
        System.out.format("Привычки с %s по %s:%n", startDate.toString(), endDate.toString());

        if(endDate.isBefore(startDate)) {
            System.out.println("Вы неправильно ввели даты! Попробуйте ещё раз.");
            return;
        }

        habitService.getByAccountId(myAccount.getId()).forEach((habit) -> {
            System.out.format("Привычка: %s%n", habit.getName());
            statisticService.getByHabitId(habit.getId()).forEach((statistic) -> {
                LocalDate date = statistic.getDate();
                Boolean status = statistic.getMarking();

                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    String statusText = status == null ? "не отмечена" : status ? "выполнена" : "не выполнена";
                    System.out.format("  Дата: %s - Статус: %s%n", date, statusText);
                }
            });
            System.out.println();
        });
    }

    public void statisticStreaks() {
        System.out.println("Серии выполнения привычек: ");

        habitService.getByAccountId(myAccount.getId()).forEach((habit) -> {
            int streak = statisticService.calcStreak(habit.getId());
            System.out.format("Привычка: %s - Текущая серия: %s%n", habit.getName(), streak);
        });
    }

    public void statisticByName() {
        List<Habit> habits = habitService.printAll(myAccount.getId());
        int i = habits.size();

        int input = inputInt("\nВведите номер привычки:", 0, i);
        Habit habit = habits.get(input);

        if (habit == null) {
            System.out.println("Привычка с таким названием не найдена.");
            return;
        }

        System.out.printf("Статистика по привычке: %s%n", habit.getName());
        System.out.printf("Описание: %s%n", habit.getDescription());
        System.out.printf("Период: %s%n", habit.getPeriod().getDays());

        statisticService.getByHabitId(habit.getId()).forEach((statistic) -> {
            Boolean status = statistic.getMarking();
            String statusText = status == null ? "не отмечена" : status ? "выполнена" : "не выполнена";
            System.out.format("  Дата: %s - Статус: %s%n", statistic.getDate().toString(), statusText);
        });
    }

    public void statisticPercentile() {
        List<Habit> habits = habitService.getByAccountId(myAccount.getId());
        if (habits.isEmpty()) {
            System.out.println("У вас нет привычек для анализа!");
            return;
        }

        habits.forEach((habit) -> {
            List<Boolean> statistics = statisticService.getByHabitId(habit.getId()).stream().map(Statistic::getMarking).toList();
            if (statistics.isEmpty()) {
                System.out.format("Привычка \"%s\" ещё не отмечена.%n", habit.getName());
                return;
            }

            long completedDays = statistics.stream().filter(Boolean.TRUE::equals).count();
            long totalDays = statistics.size();

            double completionPercentage = ((double) completedDays / totalDays) * 100;

            System.out.format("Привычка: %s - Выполнено: %.2f%% (всего дней: %d, выполнено: %d%n)\n",
                    habit.getName(), completionPercentage, totalDays, completedDays);
        });
    }
}
