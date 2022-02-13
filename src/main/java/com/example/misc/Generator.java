package com.example.misc;

import com.example.dao.GroupDao;
import com.example.model.Group;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final GroupDao groupDao;

    public Generator(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public void generateData(Connection connection, int groupCount) throws SQLException {
        generateGroups(connection, groupCount);
    }

    private List<Group> generateGroups(Connection connection, int count) throws SQLException {
        List<Group> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String groupName = generateGroupName();
            result.add(groupDao.save(connection, new Group(groupName)));
        }

        return result;
    }

    private String generateGroupName() {
        String sb = String.valueOf(randomChar(alphabet)) + randomChar(alphabet) + "-" +
                ThreadLocalRandom.current().nextInt(10, 99);
        return sb;
    }

    private char randomChar(char[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}
