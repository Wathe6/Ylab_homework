package io.envoi.menu;

import io.envoi.model.Habit;
import io.envoi.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.envoi.Main.*;

public class StatisticMenu
{
    public User myUser;
    public StatisticMenu(User myUser)
    {
        this.myUser = myUser;
    }
    public User getMyUser()
    {
        return myUser;
    }
    public void habitsStatisticMenu()
    {
        while (true)
        {
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

            switch (input)
            {
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
    public void statisticAll()
    {
        System.out.println("Список всех привычек:");
        myUser.getHabits().forEach((name, habit) -> {
            System.out.println("Привычка: " + name);
            habit.getStatistic().forEach((date, status) -> {
                String statusText = status == null ? "не отмечена" : status ? "выполнена" : "не выполнена";
                System.out.println("  Дата: " + date + " - Статус: " + statusText);
            });
            System.out.println();
        });
    }
    public void statisticByStatus()
    {
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

        System.out.println("Привычки со статусом \"" + statusText + "\":");

        myUser.getHabits().forEach((name, habit) -> {
            habit.getStatistic().forEach((date, status) -> {
                boolean matches = switch (input) {
                    case 1 -> Boolean.TRUE.equals(status);
                    case 2 -> Boolean.FALSE.equals(status);
                    case 3 -> status == null;
                    default -> false;
                };

                if (matches) {
                    System.out.println("Привычка: " + name + " - Дата: " + date + " - Статус: " + statusText);
                }
            });
        });
    }
    public void statisticByDate()
    {
        LocalDate startDate = inputDate("Введите начальную дату (гггг-мм-дд): ");
        LocalDate endDate = inputDate("Введите конечную дату (гггг-мм-дд): ");
        System.out.println("Привычки с " + startDate + " по " + endDate + ":");

        if(endDate.isBefore(startDate))
        {
            System.out.println("Вы неправильно ввели даты! Попробуйте ещё раз.");
            return;
        }

        myUser.getHabits().forEach((name, habit) -> {
            System.out.println("Привычка: " + name);
            habit.getStatistic().forEach((date, status) -> {
                if (!date.isBefore(startDate) && !date.isAfter(endDate))
                {
                    String statusText = status == null ? "не отмечена" : status ? "выполнена" : "не выполнена";
                    System.out.println("  Дата: " + date + " - Статус: " + statusText);
                }
            });
            System.out.println();
        });
    }
    public void statisticStreaks()
    {
        System.out.println("Серии выполнения привычек: ");

        myUser.getHabits().forEach((name, habit) -> {
            int streak = 0;

            List<Boolean> statuses = new ArrayList<>(habit.getStatistic().values());
            Collections.reverse(statuses);

            for (Boolean status : statuses)
            {
                if (Boolean.TRUE.equals(status))
                {
                    streak++;
                } else
                {
                    break;  // Останавливаем цикл, если встречаем false или null
                }
            }

            System.out.println("Привычка: " + name + " - Текущая серия: " + streak);
        });
    }
    public void statisticByName()
    {
        String name = inputLine("Введите название привычки: ");
        Habit habit = myUser.getHabits().get(name);

        if (habit == null)
        {
            System.out.println("Привычка с таким названием не найдена.");
            return;
        }

        System.out.println("Статистика по привычке: " + name);
        System.out.println("Описание: " + habit.getDescription());
        System.out.println("Период: " + habit.getPeriod().getDays());

        habit.getStatistic().forEach((date, status) -> {
            String statusText = status == null ? "не отмечена" : status ? "выполнена" : "не выполнена";
            System.out.println("  Дата: " + date + " - Статус: " + statusText);
        });
    }
    public void statisticPercentile()
    {
        if (myUser.getHabits().isEmpty())
        {
            System.out.println("У вас нет привычек для анализа!");
            return;
        }

        myUser.getHabits().forEach((name, habit) -> {
            Map<LocalDate, Boolean> statistics = habit.getStatistic();
            if (statistics.isEmpty()) {
                System.out.println("Привычка \"" + name + "\" ещё не отмечена.");
                return;
            }

            long completedDays = statistics.values().stream().filter(Boolean.TRUE::equals).count();
            long totalDays = statistics.size();

            double completionPercentage = ((double) completedDays / totalDays) * 100;

            System.out.printf("Привычка: %s - Выполнено: %.2f%% (всего дней: %d, выполнено: %d)\n",
                    name, completionPercentage, totalDays, completedDays);
        });
    }
}
