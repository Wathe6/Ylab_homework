package io.envoi.persistence;

import io.envoi.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersPersistence
{
    private static final String FILE_PATH = "users_data.ser";

    public static void save(Map<String, User> users)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH)))
        {
            oos.writeObject(users.values().stream().toList());
            System.out.println("Users successfully saved to file.");
        } catch (IOException e)
        {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, User> load()
    {
        File file = new File(FILE_PATH);
        if (!file.exists())
        {
            System.out.println("No users file found. Starting with an empty list.");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
        {
            Map<String, User> userMap = new HashMap<>();
            List<User> users = (List<User>) ois.readObject();
            users.forEach(u -> userMap.put(u.getEmail(), u));
            return userMap;
        } catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Error loading users from file: " + e.getMessage());
            return new HashMap<>();
        }
    }
}