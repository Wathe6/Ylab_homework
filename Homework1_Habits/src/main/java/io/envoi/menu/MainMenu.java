package io.envoi.menu;

import io.envoi.auth.Authentication;
import io.envoi.model.Account;
import io.envoi.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
/**
 * Contains main menu - calls for another menus, authentication and static input for lines, integers and dates.
 * */
public class MainMenu {
    public static Account myAccount;
    public static User myUser;
    public static Scanner in;

    public static void start() {
        try {
            in = new Scanner(System.in);
            System.out.println("""
                    Добро пожаловать в программу по отслеживанию привычек!
                    Разработал Пустовой В. А..""");
            authenticationMenu();

            while (true) {
                System.out.println("""
                        Введите цифру, чтобы продолжить:
                        0. Выход.
                        1. Открыть профиль.
                        2. Открыть привычки.
                        3. Получить статистику по привычкам.""");

                int input = inputInt("Введите номер операции: ", 0, 3);

                switch (input) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> profileMenu();
                    case 2 -> habitsMenu();
                    case 3 -> habitsStatisticMenu();
                    default -> System.out.println("Вы ввели некорректный номер!");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка в методе main: " + e.getMessage());
        } finally {
            Authentication authentication = new Authentication();

            authentication.update(myAccount, myUser);

            authentication.save();
        }
    }
    public static void authenticationMenu() {
        Authentication auth = new Authentication();
        auth.authMenu();
        myAccount = auth.getMyAccount();
        myUser = auth.getMyUser();
        auth.save();
    }
    public static void profileMenu() {
        Authentication auth = new Authentication();
        auth.profileMenu();
        myAccount = auth.getMyAccount();
        myUser = auth.getMyUser();
        auth.save();
    }
    public static void habitsMenu() {
        HabitsMenu habitsMenu = new HabitsMenu(myUser);
        habitsMenu.habitsMenu();
        myUser = habitsMenu.getMyUser();
    }
    public static void habitsStatisticMenu() {
        StatisticMenu statisticMenu = new StatisticMenu(myUser);
        statisticMenu.habitsStatisticMenu();
        myUser = statisticMenu.getMyUser();
    }
    public static String inputLine(String prompt) {
        System.out.print(prompt);
        while (true) {
            String input = in.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Пожалуйста, введите значение.");
        }
    }
    public static int inputInt(String prompt, int min, int max) {
        System.out.print(prompt);
        while (true) {
            if (in.hasNextInt()) {
                int input = in.nextInt();
                in.nextLine();  // Очистка буфера
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.format("Число должно быть между %d и %d.%n", min, max);
            } else {
                System.out.println("Пожалуйста, введите число.");
                in.nextLine();  // Очистка буфера
            }
        }
    }
    public static LocalDate inputDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Некорректная дата. Пожалуйста, введите дату в формате гггг-мм-дд.");
            }
        }
    }
}
