package io.envoi.auth;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.model.User;
import io.envoi.persistence.AccountPersistence;
import io.envoi.persistence.UsersPersistence;
import io.envoi.service.AccountsService;
import io.envoi.service.UsersService;

import java.io.Console;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Authentication
{
    public static AccountsService accountsService = new AccountsService(AccountPersistence.load());
    public static UsersService usersService = new UsersService(UsersPersistence.load());
    public static Account myAccount;
    public static User myUser;
    public static Scanner in = new Scanner(System.in);

    public void loginDialog()
    {
        while (true)
        {
            System.out.println("""
                    Введите цифру, чтобы продолжить:
                    1. Регистрация.""");
            boolean regPoolIsEmpty = accountsService.getAll().size() == 0;
            if(!regPoolIsEmpty)
            {
                System.out.println("2. Вход.");
            }
            if(in.hasNextInt())
            {
                int input = in.nextInt();
                in.nextLine();
                if (input == 1)
                {
                    if (registration())
                        return;
                } else if(!regPoolIsEmpty && input == 2)
                {
                    if (login())
                        return;
                } else
                {
                    System.out.println("Вы ввели некорректный номер!");
                }
            }
            else
            {
                System.out.println("Вы не ввели число!");
            }
        }
    }
    public Account getMyAccount()
    {
        return myAccount;
    }
    public User getMyUser()
    {
        return myUser;
    }
    private boolean registration()
    {
        System.out.println("Регистрация");

        String email;
        while (true)
        {
            System.out.print("Введите ваш email: ");
            email = in.nextLine();
            if(email == null || email.isEmpty())
            {
                System.out.println("Вы не ввели почту. Попробуйте снова.");
            }
            else if (!isValidEmail(email))
            {
                System.out.println("Неверный формат email. Попробуйте снова.");
            }
            else if(accountsService.emailExists(email))
            {
                System.out.println("Аккаунт с такой почтой уже существует. Попробуйте другую.");
            }
            else
            {
                break;
            }
        }

        String password = readPassword();

        String nickname;
        while (true)
        {
            System.out.print("Введите ваше имя: ");
            nickname = in.nextLine();
            if(nickname == null || nickname.isEmpty())
            {
                System.out.println("Вы не ввели имя. Попробуйте снова.");
            }
            else
            {
                break;
            }
        }

        if (accountsService.create(email, password, Roles.USER) &&
            usersService.create(email, nickname))
        {
            System.out.println("Регистрация прошла успешно!");
            myAccount = accountsService.getByEmail(email);
            myUser = usersService.getByEmail(email);
            return true;
        }
        else
        {
            System.out.println("Регистрация не удалась.");
            return false;
        }
    }
    private boolean login()
    {
        System.out.println("Вход.");

        String email;
        while (true)
        {
            System.out.print("Введите ваш email: ");
            email = in.nextLine();
            if (!isValidEmail(email))
            {
                System.out.println("Неверный формат email. Попробуйте снова.");
            }
            else if(!accountsService.emailExists(email))
            {
                System.out.println("Аккаунта с такой почтой не существует.");

            }
            else
            {
                break;
            }
        }

        String realPassword = accountsService.getByEmail(email).getPassword();
        if(realPassword.equals(readPassword()))
        {
            System.out.println("Вход успешный.");
            myAccount = accountsService.getByEmail(email);
            myUser = usersService.getByEmail(email);
            return true;
        }
        else
        {
            System.out.println("Пароль неправильный. Попробуйте снова.");
            return false;
        }
    }
    private String readPassword()
    {
        Console console = System.console();
        String password;
        while(true)
        {
            System.out.print("Введите пароль: ");
            //Скрывать ввод пароля, если позволяет консоль
            password = console != null ? new String(console.readPassword()) : in.nextLine();

            if(password == null || password.isEmpty())
            {
                System.out.println("Вы не ввели пароль. Попробуйте снова.");
            }
            else
            {
                return password;
            }
        }
    }
    private boolean isValidEmail(String email)
    {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    public void clean()
    {
        AccountPersistence.save(accountsService.getAll());
        UsersPersistence.save(usersService.getAll());
    }
}
