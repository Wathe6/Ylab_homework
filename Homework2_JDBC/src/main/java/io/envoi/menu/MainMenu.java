package io.envoi.menu;

import io.envoi.config.LiquibaseConfig;
import io.envoi.dao.AccountDAO;
import io.envoi.dao.HabitDAO;
import io.envoi.dao.StatisticDAO;
import io.envoi.model.Account;
import io.envoi.service.AccountService;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;

import java.sql.SQLException;
import java.util.Scanner;

import static io.envoi.in.Input.inputInt;
/**
 * Main menu, from which all other methods are used. Creates DAOs, Services and Menus when initialised.
 * */
public class MainMenu {
    private static Account myAccount;
    private static final AuthMenu authMenu;
    private static final AccountMenu accountMenu;
    private static final HabitsMenu habitsMenu;
    private static final StatisticMenu statisticMenu;

    static {
        Scanner in = new Scanner(System.in);

        AccountDAO accountDAO = new AccountDAO();
        HabitDAO habitDAO = new HabitDAO();
        StatisticDAO statisticDAO = new StatisticDAO();

        AccountService accountService = new AccountService(accountDAO);
        HabitService habitService = new HabitService(habitDAO);
        StatisticService statisticService = new StatisticService(statisticDAO);

        authMenu = new AuthMenu(accountService, in);
        accountMenu = new AccountMenu(accountService, in);
        habitsMenu = new HabitsMenu(habitService, statisticService);
        statisticMenu = new StatisticMenu(habitService, statisticService);
    }

    public static void start() {
        try {
            System.out.println("""
                    Добро пожаловать в программу по отслеживанию привычек!
                    Данная версия написана с использованием JDBC и Liquibase.
                    Разработал Пустовой В. А..""");

            authMenu.authMenu();
            myAccount = authMenu.getMyAccount();

            accountMenu.setMyAccount(myAccount);
            habitsMenu.setMyAccount(myAccount);
            statisticMenu.setMyAccount(myAccount);

            mainMenu();
        } catch (Exception e) {
            System.out.println("Ошибка в методе main: " + e.getMessage());
        } finally {
            try{
                LiquibaseConfig.close();
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии БД: " + e.getMessage());
            }
        }
    }

    public static void mainMenu() {
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
                case 1 -> myAccount = accountMenu.accountMenu();
                case 2 -> habitsMenu.habitsMenu();
                case 3 -> statisticMenu.statisticMenu();
                default -> System.out.println("Вы ввели некорректный номер!");
            }
        }
    }
}