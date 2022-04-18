package com.example.dao.datasource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SimpleDatasource implements Datasource {
    public final static String DRIVER_CLASS = "datasource.driver-class";
    public final static String JDBC_URL = "datasource.jdbc-url";
    public final static String USERNAME = "datasource.username";
    public final static String PASSWORD = "datasource.password";

    private final Driver driver;

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public SimpleDatasource(Properties properties) {
        String className = properties.getProperty(DRIVER_CLASS);
        this.jdbcUrl = properties.getProperty(JDBC_URL);
        this.username = properties.getProperty(USERNAME);
        this.password = properties.getProperty(PASSWORD);

        try {
            @SuppressWarnings("unchecked")
            Class<Driver> clazz = (Class<Driver>) Class.forName(className);
            driver = clazz.newInstance();
            DriverManager.registerDriver(driver);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public void close() {
        try {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

    }
}
