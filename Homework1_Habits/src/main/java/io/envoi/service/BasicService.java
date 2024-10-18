package io.envoi.service;

import java.util.HashMap;
import java.util.Map;
/**
 * Contains basic CRUD operations for a Map items, where string is email and T is Account or User.
 * Operations list: printAll, getALl, add, update, delete and emailExists.
 * */
public abstract class BasicService<T> {
    Map<String, T> items;
    public BasicService() {
        this.items = new HashMap<>();
    }
    public BasicService(Map<String, T> items){
        this.items = items;
    }
    public void printAll() {
        items.values().forEach(System.out::println);
    }
    public Map<String, T> getAll() {
        return new HashMap<>(items);
    }
    public T getByEmail(String email) {
        return items.get(email);
    }
    public abstract boolean add(T item);
    //public abstract boolean create();
    public abstract boolean update(T item);
    public boolean delete(String email) {
        return items.remove(email) != null;
    }
    public boolean emailExists(String email) {
        return items.containsKey(email);
    }
}
