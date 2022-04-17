package com.example.dao.mappers;

import com.example.model.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.model.Student.*;


public class StudentMapper implements Mapper<Student> {
    @Override
    public Student apply(ResultSet rs) throws SQLException {
        return new Student(rs.getLong(STUDENT_ID), rs.getLong(STUDENT_GROUP_ID), rs.getString(STUDENT_FIRST_NAME), rs.getString(STUDENT_LAST_NAME));
    }
}
