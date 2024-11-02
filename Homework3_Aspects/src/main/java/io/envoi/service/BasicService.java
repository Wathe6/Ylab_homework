package io.envoi.service;

import io.envoi.dao.BasicDAO;

import java.util.List;
/**
 * GetAll, get(id), delete, update, isTableEmpty operations with services.
 * */
public abstract class BasicService<T, DAO> {
    protected BasicDAO<T, DAO> dao;

    public BasicService(BasicDAO<T, DAO> dao) {
        this.dao = dao;
    }

    public List<T> getAll() {
        return dao.getAll();
    }

    public T get(Long id) {
        return dao.get(id);
    }
    public <V> List<T> getByField(String fieldName, V value) {
        return dao.getByField(fieldName, value);
    }
    public boolean save(T t) {
        return dao.save(t);
    }

    public <V extends Long> boolean delete(V v) {
        return dao.delete(v);
    }

    public boolean update(T t) {
        return dao.update(t);
    }
    public boolean isTableEmpty() {
        return dao.isTableEmpty();
    }
}