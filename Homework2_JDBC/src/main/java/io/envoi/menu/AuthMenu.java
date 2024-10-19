package io.envoi.menu;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.service.AccountService;

import java.util.Scanner;

import static io.envoi.in.Input.isValidEmail;
import static io.envoi.in.Input.readPassword;

public class AuthMenu {
    private AccountService accountService;
    private Scanner in;
    private Account myAccount;

    public AuthMenu(AccountService accountService, Scanner in) {
        this.accountService = accountService;
        this.in = in;
    }

    public Account getMyAccount() {
        return myAccount;
    }

    public void authMenu() {
        while (true) {
            System.out.println("""
                    Введите цифру, чтобы продолжить:
                    1. Регистрация.""");
            boolean regPoolIsEmpty = accountService.isTableEmpty();

            if(!regPoolIsEmpty) {
                System.out.println("2. Вход.");
            }

            if(in.hasNextInt()) {
                int input = in.nextInt();
                in.nextLine();
                if (input == 1) {
                    if (registration())
                        return;
                } else if(!regPoolIsEmpty && input == 2) {
                    if (login())
                        return;
                } else {
                    System.out.println("Вы ввели некорректный номер!");
                }
            } else {
                System.out.println("Вы не ввели число!");
            }
        }
    }

    private boolean registration() {
        System.out.println("Регистрация");

        String email;
        while (true) {
            System.out.print("Введите ваш email: ");
            email = in.nextLine();
            if(email == null || email.isEmpty()) {
                System.out.println("Вы не ввели почту. Попробуйте снова.");
            } else if (!isValidEmail(email)) {
                System.out.println("Неверный формат email. Попробуйте снова.");
            } else if(accountService.emailExists(email)) {
                System.out.println("Аккаунт с такой почтой уже существует. Попробуйте другую.");
            } else {
                break;
            }
        }

        String password = readPassword("Введите пароль: ");

        String nickname;
        while (true) {
            System.out.print("Введите ваше имя: ");
            nickname = in.nextLine();
            if(nickname == null || nickname.isEmpty()) {
                System.out.println("Вы не ввели имя. Попробуйте снова.");
            } else {
                break;
            }
        }

        if (accountService.save(new Account(email, password, nickname, Roles.USER))) {
            System.out.println("Регистрация прошла успешно!");
            myAccount = accountService.getByEmail(email);
            return true;
        } else {
            System.out.println("Регистрация не удалась.");
            return false;
        }
    }

    private boolean login() {
        System.out.println("Вход.");

        String email;
        while (true) {
            System.out.print("Введите email: ");
            email = in.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Неверный формат email. Попробуйте снова.");
            } else if(!accountService.emailExists(email)) {
                System.out.println("Аккаунта с такой почтой не существует.");
            } else {
                break;
            }
        }

        String realPassword = accountService.getByEmail(email).getPassword();
        if(realPassword.equals(readPassword("Введите пароль: "))) {
            System.out.println("Вход успешный.");
            myAccount = accountService.getByEmail(email);
            return true;
        } else {
            System.out.println("Пароль неправильный. Попробуйте снова.");
            return false;
        }
    }
}
