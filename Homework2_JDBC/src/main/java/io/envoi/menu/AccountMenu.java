package io.envoi.menu;

import io.envoi.model.Account;
import io.envoi.service.AccountService;

import java.util.Scanner;

import static io.envoi.in.Input.*;
/**
 * Menu for changes in user Acccount - change Email, Password, Name or Delete Account.
 * */
public class AccountMenu {
    private AccountService accountService;
    private Scanner in;
    private Account myAccount;

    public AccountMenu(AccountService accountService, Scanner in) {
        this.accountService = accountService;
        this.in = in;
    }

    public void setMyAccount(Account account) {
        this.myAccount = account;
    }

    public Account accountMenu() {
        while (true) {
            System.out.println("""
                    Введите цифру, чтобы продолжить:
                    0. Выход.
                    1. Изменить пароль.
                    2. Изменить почту.
                    3. Изменить имя.
                    4. Удалить аккаунт.""");

            int input = inputInt("Введите номер операции: ", 0, 4);

            switch (input) {
                case 0 -> {return myAccount;}
                case 1 -> {
                    if (changePassword()) {
                        System.out.println("Пароль успешно изменён!");
                        return myAccount;
                    } else {
                        System.out.println("Ошибка при смене пароля!");
                    }
                }
                case 2 -> {
                    if (changeEmail()) {
                        System.out.println("Почта успешно изменена!");
                        return myAccount;
                    } else {
                        System.out.println("Ошибка при смене почты!");
                    }
                }
                case 3 -> {
                    if (changeNickname()) {
                        System.out.println("Имя успешно изменено!");
                        return myAccount;
                    } else {
                        System.out.println("Ошибка при смене имени!");
                    }
                }
                case 4 -> {
                    String answer = inputLine("Вы уверены, что хотите удалить ваш аккаунт? (YES\\NO)\n");

                    if (answer.equalsIgnoreCase("no")) {
                        return myAccount;
                    }

                    if (deleteAcc()) {
                        System.out.println("Аккаунт успешно удалён!");
                        System.exit(0);
                    } else {
                        System.out.println("Ошибка при удалении аккаунта!");
                    }
                }
                default -> System.out.println("Вы ввели некорректный номер!");
            }
        }
    }

    private boolean changeEmail() {
        String newEmail = inputLine("Введите новую почту: ");
        String oldEmail = myAccount.getEmail();

        if(!isValidEmail(newEmail)) {
            System.out.println("Неверный формат email. Попробуйте снова.");
            return false;
        }
        if(accountService.emailExists(newEmail)) {
            System.out.println("Данная почта занята. Попробуйте другую.");
            return false;
        }
        if(newEmail.equals(oldEmail)) {
            System.out.println("Вы ввели туже почту. Попробуйте другую.");
            return false;
        }

        myAccount.setEmail(newEmail);
        return accountService.update(myAccount);
    }

    private boolean changePassword() {
        String oldPassword = readPassword("Введите старый пароль: ");
        if (!myAccount.getPassword().equals(oldPassword)) {
            System.out.println("Неверный пароль!");
            return false;
        }

        String newPassword = readPassword("Введите новый пароль: ");
        if(myAccount.getPassword().equals(newPassword)) {
            System.out.println("Вы ввели ваш старый пароль!");
            return false;
        }

        myAccount.setPassword(newPassword);
        accountService.update(myAccount);
        System.out.println(accountService.getByEmail("admin@example.com").toString());

        return true;
    }

    private boolean changeNickname() {
        String newName = inputLine("Введите новое имя: ");
        if(myAccount.getName().equals(newName)) {
            System.out.println("Вы ввели такое же имя!");
            return false;
        }

        myAccount.setName(newName);
        return true;
    }

    private boolean deleteAcc() {
        return accountService.delete(myAccount.getId());
    }
}
