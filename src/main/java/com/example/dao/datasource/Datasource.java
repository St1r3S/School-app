package com.example.dao.datasource;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public interface Datasource extends Closeable {
    Connection getConnection() throws SQLException;
}
