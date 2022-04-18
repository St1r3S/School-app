package com.example.dao.impl;

import com.example.dao.AbstractCrudDao;
import com.example.dao.CourseDao;
import com.example.dao.mappers.CourseMapper;
import com.example.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDaoImpl extends AbstractCrudDao<Course, Long> implements CourseDao {

    public static final String SELECT_ONE = "SELECT * FROM courses where course_id = ?";
    public static final String FIND_BY_NAME = "SELECT * FROM courses where course_name = ?";
    public static final String SELECT_ALL = "SELECT * FROM courses";
    public static final String INSERT_ONE = "INSERT INTO courses (course_name,course_description) values (?,?)";
    public static final String UPDATE = "UPDATE courses SET course_name = ?, course_description = ? where course_id = ?";
    public static final String DELETE_ONE = "DELETE FROM courses WHERE course_id = ?";
    public static final String FIND_BY_STUDENT_ID = "SELECT c.course_id, c.course_name, c.course_description FROM courses AS c " +
            "INNER JOIN students_courses AS sc " +
            "ON c.course_id = sc.course_id " +
            "INNER JOIN students AS s " +
            "ON sc.student_id = s.student_id where s.student_id = ?";

    private final CourseMapper mapper;

    public CourseDaoImpl() {
        this.mapper = new CourseMapper();
    }

    @Override
    protected Course create(Connection connection, Course entity) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement(INSERT_ONE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to create course " + entity);
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Unable to retrieve id");
                }
                Long id = rs.getLong(1);
                return new Course(id, entity.getName(), entity.getDescription());
            }
        }
    }

    @Override
    protected Course update(Connection connection, Course entity) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setLong(3, entity.getId());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to update course " + entity);
            }
            return new Course(entity.getId(), entity.getName(), entity.getDescription());
        }
    }

    @Override
    public Optional<Course> findByName(Connection connection, String name) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(FIND_BY_NAME)) {
            ps.setString(1, name);
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
    public List<Course> findByStudentId(Connection connection, Long studentId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(FIND_BY_STUDENT_ID)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Course> courses = new ArrayList<>();
                while (rs.next()) {
                    courses.add(mapper.apply(rs));
                }
                return courses;
            }
        }
    }

    @Override
    public Optional<Course> findById(Connection connection, Long id) throws SQLException {
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
    public List<Course> findAll(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = statement.executeQuery()) {
                List<Course> courses = new ArrayList<>();
                while (rs.next()) {
                    courses.add(mapper.apply(rs));
                }
                return courses;
            }
        }
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_ONE)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to delete course (id = " + id + ")");
            }
        }
    }
}
