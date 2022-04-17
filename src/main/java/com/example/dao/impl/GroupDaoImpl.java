package com.example.dao.impl;

import com.example.dao.AbstractCrudDao;
import com.example.dao.GroupDao;
import com.example.dao.mappers.GroupMapper;
import com.example.model.Group;
import com.example.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDaoImpl extends AbstractCrudDao<Group, Long> implements GroupDao {

    public static final String SELECT_ONE = "SELECT * FROM groups where group_id = ?";
    public static final String FIND_BY_NAME = "SELECT * FROM groups where group_name = ?";
    public static final String SELECT_ALL = "SELECT * FROM groups";
    public static final String SELECT_ALL_WHERE_SUFFICIENT_STUDENTS_AMOUNT = "SELECT g.group_id, g.group_name FROM groups AS g " +
            "LEFT JOIN students AS s ON g.group_id = s.group_id GROUP BY g.group_id HAVING count(s.student_id) <= ?";
    public static final String INSERT_ONE = "INSERT INTO groups (group_name) values (?)";
    public static final String UPDATE = "UPDATE groups SET group_name = ? where group_id = ?";
    public static final String DELETE_ONE = "DELETE FROM groups WHERE group_id = ?";

    private final GroupMapper mapper;

    public GroupDaoImpl() {
        this.mapper = new GroupMapper();
    }

    @Override
    public Optional<Group> findById(Connection connection, Long id) throws SQLException {
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
    public List<Group> findAll(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = statement.executeQuery()) {
                List<Group> groups = new ArrayList<>();
                while (rs.next()) {
                    groups.add(mapper.apply(rs));
                }
                return groups;
            }
        }
    }

    @Override
    protected Group create(Connection connection, Group entity) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement(INSERT_ONE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to create group " + entity);
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Unable to retrieve id");
                }
                Long id = rs.getLong(1);
                return new Group(id, entity.getName());
            }
        }
    }

    @Override
    protected Group update(Connection connection, Group entity) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE)) {
            ps.setString(1, entity.getName());
            ps.setLong(2, entity.getId());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to update group " + entity);
            }
            return new Group(entity.getId(), entity.getName());
        }
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_ONE)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to delete group (id = " + id + ")");
            }
        }
    }

    @Override
    public Optional<Group> findByName(Connection connection, String name) throws SQLException {
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
    public List<Group> findAllByStudentsAmount(Connection connection, int amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_WHERE_SUFFICIENT_STUDENTS_AMOUNT)) {
            statement.setInt(1, amount);
            try (ResultSet rs = statement.executeQuery()) {
                List<Group> groups = new ArrayList<>();
                while (rs.next()) {
                    groups.add(mapper.apply(rs));
                }
                return groups;
            }
        }
    }
}
