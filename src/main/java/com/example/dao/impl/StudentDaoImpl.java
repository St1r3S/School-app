package com.example.dao.impl;

import com.example.dao.AbstractCrudDao;
import com.example.dao.StudentDao;
import com.example.dao.mappers.StudentMapper;
import com.example.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl extends AbstractCrudDao<Student, Long> implements StudentDao {

    public static final String SELECT_ONE = "SELECT * FROM students where student_id = ?";
    public static final String FIND_BY_FIRSTNAME_AND_LASTNAME = "SELECT * FROM students where first_name = ? and last_name = ?";
    public static final String FIND_BY_GROUP_ID = "SELECT * FROM students WHERE group_id = ?";
    public static final String SELECT_ALL = "SELECT * FROM students";
    public static final String INSERT_ONE = "INSERT INTO students (group_id, first_name, last_name) values (?,?,?)";
    public static final String UPDATE = "UPDATE students SET group_id = ?, first_name = ?, last_name = ?  WHERE student_id = ?";
    public static final String DELETE_ONE = "DELETE FROM students WHERE student_id = ?";
    public static final String FIND_BY_COURSE_ID = "SELECT s.student_id, s.group_id, s.first_name, s.last_name FROM students AS s " +
            "INNER JOIN students_courses AS sc ON s.student_id = sc.student_id INNER JOIN courses AS c ON sc.course_id = c.course_id where c.course_id = ?";
    public static final String FIND_BY_COURSE_NAME = "SELECT s.student_id, s.group_id, s.first_name, s.last_name FROM students AS s " +
            "INNER JOIN students_courses AS sc ON s.student_id = sc.student_id INNER JOIN courses AS c ON sc.course_id = c.course_id where c.course_name = ?";
    public static final String DELETE_MANY_TO_MANY = "DELETE FROM students_courses where student_id = ? AND course_id = ?";
    public static final String INSERT_MANY_TO_MANY = "INSERT INTO students_courses (student_id, course_id) values (?,?)";

    private final StudentMapper mapper;

    public StudentDaoImpl() {
        this.mapper = new StudentMapper();
    }

    @Override
    public Student create(Connection connection, Student entity) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement(INSERT_ONE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, entity.getGroupId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to create student " + entity);
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Unable to retrieve id");
                }
                Long id = rs.getLong(1);
                return new Student(id, entity.getGroupId(), entity.getFirstName(), entity.getLastName());
            }
        }
    }

    @Override
    public Student update(Connection connection, Student entity) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE)) {
            ps.setLong(1, entity.getGroupId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setLong(4, entity.getId());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to update student " + entity);
            }
            return new Student(entity.getId(), entity.getGroupId(), entity.getFirstName(), entity.getLastName());
        }
    }

    @Override
    public Optional<Student> findById(Connection connection, Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ONE)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.apply(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public List<Student> findAll(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = statement.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (rs.next()) {
                    students.add(mapper.apply(rs));
                }
                return students;
            }
        }
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_ONE)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to delete student (id = " + id + ")");
            }
        }
    }

    @Override
    public Optional<Student> findByFirstNameAndLastName(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(FIND_BY_FIRSTNAME_AND_LASTNAME)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.apply(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public List<Student> findStudentsByGroupId(Connection connection, Long groupId) throws SQLException {
        return getStudents(connection, groupId, FIND_BY_GROUP_ID);
    }

    @Override
    public List<Student> findByCourseId(Connection connection, Long courseId) throws SQLException {
        return getStudents(connection, courseId, FIND_BY_COURSE_ID);
    }

    @Override
    public List<Student> findByCourseName(Connection connection, String courseName) throws SQLException {
        if (courseName != null)
            try (PreparedStatement ps = connection.prepareStatement(FIND_BY_COURSE_NAME)) {
                ps.setString(1, courseName);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Student> students = new ArrayList<>();
                    while (rs.next()) {
                        students.add(mapper.apply(rs));
                    }
                    return students;
                }
            }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public void enroll(Connection connection, Long studentId, Long courseId) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement(INSERT_MANY_TO_MANY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to create student_courses");
            }
        }
    }

    @Override
    public void expel(Connection connection, Long studentId, Long courseId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_MANY_TO_MANY)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to delete student_course (student_id = " + studentId + ", course_id = " + courseId + ")");
            }
        }
    }


    private List<Student> getStudents(Connection connection, Long courseId, String query) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (rs.next()) {
                    students.add(mapper.apply(rs));
                }
                return students;
            }
        }
    }

}
