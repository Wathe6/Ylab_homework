package io.envoi.service;

import io.envoi.dao.BasicDAO;

import java.util.List;

public abstract class BasicService<T> {
    protected BasicDAO<T> dao;

    public BasicService(BasicDAO<T> dao) {
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

    public boolean delete(T t) {
        return dao.delete(t);
    }

    public boolean update(T t) {
        return dao.update(t);
    }
    public boolean isTableEmpty() {
        return dao.isTableEmpty();
    }
}