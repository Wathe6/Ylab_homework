package io.envoi.persistence;

import io.envoi.model.Account;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountPersistence
{
    private static final String FILE_PATH = "accounts_data.ser";

    public static void save(Map<String, Account> accounts)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH)))
        {
            oos.writeObject(accounts.values().stream().toList());
            System.out.println("Аккаунты успешно сохранены.");
        } catch (IOException e)
        {
            System.err.println("Ошибка при сохранении аккаунтов: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Account> load()
    {
        File file = new File(FILE_PATH);
        if (!file.exists())
        {
            System.out.println("Файл с аккаунтами не найден.");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
        {
            Map<String, Account> accountMap = new HashMap<>();
            List<Account> accounts = (List<Account>) ois.readObject();
            accounts.forEach(a -> accountMap.put(a.getEmail(), a));
            return accountMap;
        } catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Ошибка при чтении аккаунтов " + e.getMessage());
            return new HashMap<>();
        }
    }
}
