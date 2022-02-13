package com.example.dao.mappers;

import com.example.model.Group;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.model.Group.GROUP_ID;
import static com.example.model.Group.GROUP_NAME;

public class GroupMapper implements Mapper<Group> {

    @Override
    public Group apply(ResultSet rs) throws SQLException {
        return new Group(rs.getLong(GROUP_ID), rs.getString(GROUP_NAME));
    }
}
