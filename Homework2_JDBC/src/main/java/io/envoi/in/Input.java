package io.envoi.in;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 * Input and validation methods.
 * */
public class Input {
    private static final Scanner in = new Scanner(System.in);

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

    /**
     * If the console can, makes password entry invisible
     */
    public static String readPassword(String prompt) {
        Console console = System.console();
        String password;
        while(true) {
            System.out.print(prompt);
            password = console != null ? new String(console.readPassword()) : in.nextLine();

            if(password == null || password.isEmpty()) {
                System.out.println("Вы не ввели пароль. Попробуйте снова.");
            } else {
                return password;
            }
        }
    }

    /**
     * Email check for invalid symbols.
     * */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
