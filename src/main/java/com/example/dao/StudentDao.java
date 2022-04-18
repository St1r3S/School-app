package com.example.dao;

import com.example.model.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StudentDao extends CrudDao<Student, Long> {
    Optional<Student> findByFirstNameAndLastName(Connection connection, String firstName, String lastName) throws SQLException;

    List<Student> findStudentsByGroupId(Connection connection, Long groupId) throws SQLException;

    List<Student> findByCourseId(Connection connection, Long courseId) throws SQLException;

    List<Student> findByCourseName(Connection connection, String courseName) throws SQLException;

    void enroll(Connection connection, Long studentId, Long courseId) throws SQLException;

    void expel(Connection connection, Long studentId, Long courseId) throws SQLException;
}
