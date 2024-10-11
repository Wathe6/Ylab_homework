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
            System.out.println("Accounts successfully saved to file.");
        } catch (IOException e)
        {
            System.err.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Account> load()
    {
        File file = new File(FILE_PATH);
        if (!file.exists())
        {
            System.out.println("No accounts file found. Starting with an empty list.");
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
            System.err.println("Error loading accounts from file: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
