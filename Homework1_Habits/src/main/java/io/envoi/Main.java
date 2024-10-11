package io.envoi;

import io.envoi.auth.Authentication;
import io.envoi.model.*;
import io.envoi.service.*;
import io.envoi.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main
{
    public static Account myAccount;
    public static User myUser;
    public static Scanner in;
    public static void main(String[] args)
    {
        try
        {
            in = new Scanner(System.in);
            System.out.println("""
                    Добро пожаловать в программу по отслеживанию привычек!
                    Разработал Пустовой В. А..""");
            authenticationMenu();


            while (true)
            {
                System.out.println("""
                        Введите цифру, чтобы продолжить:
                        0. Выход.
                        1. Открыть профиль.
                        2. Открыть привычки.""");
                if (in.hasNextInt())
                {
                    int input = in.nextInt();
                    in.nextLine();  // Очистка потока ввода
                    switch (input)
                    {
                        case 0 ->
                        {
                            return;
                        }
                        case 1 -> profileMenu();
                        case 2 -> habitsMenu();
                        default -> System.out.println("Вы ввели некорректный номер!");
                    }
                    if (input == 1 || input == 2)
                        break;
                } else
                {
                    System.out.println("Вы не ввели число!");
                    in.nextLine();  // Очистка потока ввода
                }
            }
        } catch(Exception e)
        {
            System.out.println("Ошибка в методе main: " + e.getMessage());
        } finally
        {
            AccountsService accountsService = new AccountsService(AccountPersistence.load());
            UsersService usersService = new UsersService(UsersPersistence.load());
            accountsService.update(myAccount);
            usersService.update(myUser);
            AccountPersistence.save(accountsService.getAll());
            UsersPersistence.save(usersService.getAll());
        }
    }

    public static void authenticationMenu()
    {
        Authentication auth = new Authentication();
        auth.loginDialog();
        myAccount = auth.getMyAccount();
        myUser = auth.getMyUser();
        auth.clean();
    }
    public static void profileMenu()
    {
        while (true)
        {
            System.out.println("""
                Введите цифру, чтобы продолжить:
                1. Изменить пароль.
                2. Изменить имя.""");
            if(in.hasNextInt())
            {
                int input = in.nextInt();
                in.nextLine();
                switch (input)
                {
                    case 1 -> {
                        if (changePassword())
                            return;
                    }
                    case 2 -> {
                        if (changeName())
                            return;
                    }
                    default -> System.out.println("Вы ввели некорректный номер!");
                }
            }
            else
            {
                System.out.println("Вы не ввели число!");
            }
        }
    }
    public static void habitsMenu()
    {
        while (true)
        {
            System.out.println("""
            Введите цифру, чтобы продолжить:
            0. Выход.
            1. Отметить привычки.
            2. Добавить привычку.
            3. Получить статистку по привычкам.""");

            if (in.hasNextInt())
            {
                int input = in.nextInt();
                in.nextLine();  // Очистка потока ввода
                switch (input)
                {
                    case 0 -> {return;}
                    case 1 -> habitsCheckMenu();
                    case 2 -> habitsAddMenu();
                    default -> System.out.println("Вы ввели некорректный номер!");
                }
            }
            else
            {
                System.out.println("Вы не ввели число!");
                in.nextLine();  // Очистка потока ввода
            }
        }
    }
    public static void habitsCheckMenu()
    {
        if (myUser.getHabits().isEmpty())
        {
            System.out.println("У вас нет привычек!");
            return;
        }

        List<Habit> canBeChecked = new ArrayList<>();
        List<Habit> cannotBeChecked = new ArrayList<>();
        //Разделяем привычки
        myUser.getHabits().forEach((habitName, habit) -> {
            if(habit.canBeChecked())
            {
                canBeChecked.add(habit);
            } else
            {
                cannotBeChecked.add(habit);
            }
        });

        do
        {
            System.out.println("Список привычек, которые можно отметить:\n0. Выход.");
            int i = 0;
            for (Habit habit : canBeChecked)
            {
                i++;
                System.out.println(i + ". " + habit.getName());
            }
            System.out.println("\nСписок привычек, которые нельзя отметить:");
            for (Habit habit : cannotBeChecked)
            {
                LocalDate lastDate = habit.getStatistic().keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.now());
                System.out.println(habit.getName() + " - " + lastDate.plus(habit.getPeriod()));
            }

            while (true)
            {
                int input = inputInt("\nВведите номер привычки:", 0, i);

                if (input == 0)
                {
                    return;
                }

                if (input > i || input < 0)
                {
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
    public static void habitsAddMenu()
    {
        System.out.println();
        String name, description;
        Period period;
        while (true)
        {
            name  = inputLine("Введите название: ");

            if(myUser.getHabits().containsKey(name))
            {
                System.out.println("Привычка с таким именем уже существует!");
                continue;
            }

            description = inputLine("Введите описание: ");

            int input = inputInt("Введите период в днях: ", 1, 30);
            period = Period.ofDays(input);

            if(myUser.addHabit(new Habit(name, description, period)))
            {
                System.out.println("Привычка успешно добавлена!");
                break;
            }
            System.out.println("Привычка не было добавлена, попробуйте ещё раз.");
        }
    }

    public static String inputLine(String prompt)
    {
        System.out.print(prompt);
        while (true)
        {
            String input = in.nextLine().trim();
            if (!input.isEmpty())
            {
                return input;
            }
            System.out.println("Пожалуйста, введите значение.");
        }
    }
    public static int inputInt(String prompt, int min, int max)
    {
        System.out.print(prompt);
        while (true)
        {
            if (in.hasNextInt())
            {
                int input = in.nextInt();
                in.nextLine();  // Очистка буфера
                if (input >= min && input <= max)
                {
                    return input;
                }
                System.out.println("Число должно быть между " + min + " и " + max + ".");
            } else
            {
                System.out.println("Пожалуйста, введите число.");
                in.nextLine();  // Очистка буфера
            }
        }
    }

    public static boolean changePassword()
    {
        String oldPassword;

        oldPassword = inputLine("Введите старый пароль: ");
        if (!myAccount.getPassword().equals(oldPassword))
        {
            System.out.println("Неверный пароль!");
        }

        String newPassword = inputLine("Введите новый пароль: ");
        myAccount.setPassword(newPassword);
        System.out.println("Пароль успешно изменен!");
        return true;
    }

    public static boolean changeName()
    {
        String newName = inputLine("Введите новое имя: ");
        myUser.setName(newName);
        System.out.println("Имя успешно изменено!");
        return true;
    }
}