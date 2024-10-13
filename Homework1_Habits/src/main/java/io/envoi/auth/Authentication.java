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

import static io.envoi.Main.inputInt;
import static io.envoi.Main.inputLine;

public class Authentication
{
    public static AccountsService accountsService = new AccountsService(AccountPersistence.load());
    public static UsersService usersService = new UsersService(UsersPersistence.load());
    public static Account myAccount;
    public static User myUser;
    public static Scanner in = new Scanner(System.in);

    public void authMenu()
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
    public void profileMenu()
    {
        while (true)
        {
            System.out.println("""
                    Введите цифру, чтобы продолжить:
                    0. Выход.
                    1. Изменить пароль.
                    2. Изменить почту.
                    3. Изменить имя.
                    4. Удалить аккаунт.""");

            int input = inputInt("Введите номер операции: ", 0, 4);

            switch (input)
            {
                case 0 -> {return;}
                case 1 ->
                {
                    if (changePassword())
                    {
                        return;
                    } else
                    {
                        System.out.println("Ошибка при смене пароля!");
                    }
                }
                case 2 ->
                {
                    String newEmail = inputLine("Введите новую почту:");
                    if (changeEmail(newEmail, myAccount.getEmail()))
                    {
                        return;
                    } else
                    {
                        System.out.println("Ошибка при смене почты!");
                    }
                }
                case 3 ->
                {
                    if (changeName())
                    {
                        return;
                    } else
                    {
                        System.out.println("Ошибка при смене имени!");
                    }
                }
                case 4 ->
                {
                    String answer = inputLine("Вы уверены, что хотите удалить аккаунт? (YES\\NO)\n");

                    if (answer.equalsIgnoreCase("no"))
                    {
                        return;
                    }

                    if (deleteAcc(myAccount.getEmail()))
                    {
                        System.out.println("Аккаунт успешно удалён!");
                        System.exit(0);
                    } else
                    {
                        System.out.println("Ошибка при удалении аккаунта!");
                    }
                }
                default -> System.out.println("Вы ввели некорректный номер!");
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

        String password = readPassword("Введите пароль: ");

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
            System.out.print("Введите email: ");
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
        if(realPassword.equals(readPassword("Введите пароль: ")))
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
    private String readPassword(String prompt)
    {
        Console console = System.console();
        String password;
        while(true)
        {
            System.out.print(prompt);
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

    public boolean changeEmail(String newEmail, String oldEmail)
    {
        if(!isValidEmail(newEmail))
        {
            System.out.println("Неверный формат email. Попробуйте снова.");
            return false;
        }
        if(emailExist(newEmail))
        {
            System.out.println("Данная почта занята. Попробуйте другую.");
            return false;
        }

        delete(oldEmail);

        myAccount.setEmail(newEmail);
        myUser.setEmail(newEmail);

        accountsService.add(myAccount);
        usersService.add(myUser);

        save();

        System.out.println("Email успешно изменен на " + newEmail);
        return true;
    }
    public boolean changePassword()
    {
        String oldPassword;

        oldPassword = readPassword("Введите старый пароль: ");
        if (!myAccount.getPassword().equals(oldPassword))
        {
            System.out.println("Неверный пароль!");
        }

        String newPassword = readPassword("Введите новый пароль: ");
        myAccount.setPassword(newPassword);
        System.out.println("Пароль успешно изменен!");
        return true;
    }
    public boolean changeName()
    {
        String newName = inputLine("Введите новое имя: ");
        myUser.setName(newName);
        System.out.println("Имя успешно изменено!");
        return true;
    }

    public boolean deleteAcc(String email)
    {
        if(!emailExist(email))
        {
            System.out.println("Аккаунта с такой почтой не существует!");
            return false;
        }

        delete(email);

        save();
        return true;
    }
    private boolean isValidEmail(String email)
    {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    private boolean emailExist(String email)
    {
        return accountsService.emailExists(email) && usersService.emailExists(email);
    }
    private void delete(String email)
    {
        accountsService.delete(email);
        usersService.delete(email);
    }
    public void update(Account account, User user)
    {
        accountsService.update(account);
        usersService.update(user);
    }
    public void save()
    {
        AccountPersistence.save(accountsService.getAll());
        UsersPersistence.save(usersService.getAll());
    }
}