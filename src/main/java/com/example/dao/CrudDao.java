package com.example.dao;

import com.example.model.Entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudDao<T extends Entity<K>, K> {
    Optional<T> findById(Connection connection, K id) throws SQLException;

    List<T> findAll(Connection connection) throws SQLException;

    T save(Connection connection, T entity) throws SQLException;

    void deleteById(Connection connection, K id) throws SQLException;
}
