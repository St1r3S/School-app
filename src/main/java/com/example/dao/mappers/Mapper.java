package com.example.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T apply(ResultSet rs) throws SQLException;
}
