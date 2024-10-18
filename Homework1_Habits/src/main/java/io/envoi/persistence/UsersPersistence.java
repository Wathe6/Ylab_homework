package io.envoi.persistence;

import io.envoi.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Saves user data from UserService to file FILE_PATH and loads it back.
 * */
public class UsersPersistence {
    private static final String FILE_PATH = "users_data.ser";

    public static void save(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users.values().stream().toList());
            System.out.println("Профили успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении профилей: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, User> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Файл с профилями не найден.");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<String, User> userMap = new HashMap<>();
            List<User> users = (List<User>) ois.readObject();
            users.forEach(u -> userMap.put(u.getEmail(), u));
            return userMap;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при чтении профилей: " + e.getMessage());
            return new HashMap<>();
        }
    }
}