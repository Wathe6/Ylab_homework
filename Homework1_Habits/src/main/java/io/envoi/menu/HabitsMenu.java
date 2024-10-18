package io.envoi.menu;

import io.envoi.model.Habit;
import io.envoi.model.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static io.envoi.menu.MainMenu.inputInt;
import static io.envoi.menu.MainMenu.inputLine;

/**
 * Contains habits menu - check, add, change and delete logic. For statistic menu see @StatisticMenu.
 * */
public class HabitsMenu {
    public User myUser;
    public HabitsMenu(User myUser)
    {
        this.myUser = myUser;
    }
    public User getMyUser()
    {
        return myUser;
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
        if (myUser.getHabits().isEmpty()) {
            System.out.println("У вас нет привычек!");
            return;
        }

        List<Habit> canBeChecked = new ArrayList<>();
        List<Habit> cannotBeChecked = new ArrayList<>();
        //Разделяем привычки
        myUser.getHabits().forEach((habitName, habit) -> {
            if (habit.canBeChecked()) {
                canBeChecked.add(habit);
            } else {
                cannotBeChecked.add(habit);
            }
        });

        do {
            System.out.println("Список привычек, которые можно отметить:\n0. Выход.");
            int i = 0;
            for (Habit habit : canBeChecked) {
                i++;
                System.out.println(i + ". " + habit.getName());
            }
            System.out.println("\nСписок привычек, которые нельзя отметить:");
            for (Habit habit : cannotBeChecked) {
                LocalDate lastDate = habit.getStatistic().keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.now());
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

                canBeChecked.get(input - 1).check();
                cannotBeChecked.add(canBeChecked.get(input - 1));
                System.out.println("Привычка " + canBeChecked.get(input - 1).getName() + " успешно отмечена!");
                canBeChecked.remove(input - 1);
                break;
            }
        } while (true);
    }
    public void habitsAddMenu() {
        String name, description;
        Period period;
        while (true) {
            name = inputLine("Введите название: ");

            if (myUser.getHabits().containsKey(name)) {
                System.out.println("Привычка с таким именем уже существует!");
                continue;
            }

            description = inputLine("Введите описание: ");

            int input = inputInt("Введите период в днях: ", 1, 30);
            period = Period.ofDays(input);

            if (myUser.addHabit(new Habit(name, description, period))) {
                System.out.println("Привычка успешно добавлена!");
                break;
            }
            System.out.println("Привычка не было добавлена, попробуйте ещё раз.");
        }
    }
    public void habitsChangeMenu() {
        String name, description;
        Period period;
        while (true) {
            name = inputLine("Введите название: ");

            if (!myUser.getHabits().containsKey(name)) {
                System.out.println("Привычки с таким именем не существует!");
                continue;
            }

            description = inputLine("Введите описание: ");

            int input = inputInt("Введите период в днях: ", 1, 30);
            period = Period.ofDays(input);

            Habit habit = myUser.getHabits().get(name);
            habit.setDescription(description);
            habit.setPeriod(period);

            myUser.getHabits().remove(name);
            if (myUser.addHabit(habit)) {
                System.out.println("Привычка успешно изменена!");
                break;
            }
            System.out.println("Привычка не было изменена, попробуйте ещё раз.");
        }

    }
    public void habitsDeleteMenu() {
        String name;

        name = inputLine("Введите название: ");

        if (!myUser.getHabits().containsKey(name)) {
            System.out.println("Привычки с таким именем не существует!");
            return;
        }

        myUser.getHabits().remove(name);
        System.out.println("Привычка успешно удалена!");
    }
}
