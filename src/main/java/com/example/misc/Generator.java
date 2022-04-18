package com.example.misc;

import com.example.dao.CourseDao;
import com.example.dao.GroupDao;
import com.example.dao.StudentDao;
import com.example.model.Course;
import com.example.model.Group;
import com.example.model.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final String[] firstnames = new String[]{"James", "Robert", "Pablo", "Michael", "Joseph",
            "Elizabeth", "Jennifer", "Sarah", "Timothy", "Amy",
            "Jonathan", "Katherine", "Alexander", "Jack", "Maria",
            "Peter", "Christina", "Homer", "Harold", "Ann"};

    private static final String[] lastnames = new String[]{"Smith", "Johnson", "Williams", "Brown", "Jones",
            "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
            "Thomas", "Taylor", "Moore", "Jackson", "Martin"};

    public static final String[] courses = new String[]{"Management", "Engineering", "Designing", "Journalism", "Accounting",
            "Law", "Economics", "Estate Management", "Medicine And Surgery", "Nursing Science"};

    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;

    public Generator(GroupDao groupDao, StudentDao studentDao, CourseDao courseDao) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
    }

    public void generateData(Connection connection, int groupCount, int studentCount) throws SQLException {
        generateGroups(connection, groupCount);
        generateStudents(connection, studentCount);
        generateCourses(connection);
        fillStudentsWithCourses(connection);
    }

    private List<Group> generateGroups(Connection connection, int count) throws SQLException {
        List<Group> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String groupName = generateGroupName();
            result.add(groupDao.save(connection, new Group(groupName)));
        }

        return result;
    }

    private List<Course> generateCourses(Connection connection) throws SQLException {
        List<Course> result = new ArrayList<>();
        for (String course : courses) {
            result.add(courseDao.save(connection, new Course(course, "In this course, you'll learn how to " + course)));
        }
        return result;
    }


    private void fillStudentsWithCourses(Connection connection) throws SQLException {
        List<Student> students = studentDao.findAll(connection);
        Long courseId;
        int i;
        for (Student student : students) {
            i = 0;
            do {
                courseId = randomCourse(connection);
                if (!studentDao.findByCourseId(connection, courseId).contains(student)) {
                    studentDao.enroll(connection, student.getId(), courseId);
                    i++;
                }
            } while (i < 3);
        }
    }

    private List<Student> generateStudents(Connection connection, int count) throws SQLException {
        List<Student> result = new ArrayList<>();
        List<Group> groups = groupDao.findAll(connection); //// na podumat
        int amount = ThreadLocalRandom.current().nextInt(10, 31);
        for (Group group : groups) {
            for (int i = 0; i < amount; i++) {
                String studentFirstName = randomFirstName(firstnames);
                String studentLastName = randomLastName(lastnames);
                result.add(studentDao.save(connection, new Student(group.getId(), studentFirstName, studentLastName)));
            }
        }
        count -= studentDao.findAll(connection).size();
        Long groupId;
        for (int i = 0; i < count; i++) {
            String studentFirstName = randomFirstName(firstnames);
            String studentLastName = randomLastName(lastnames);
            do {
                groupId = randomGroupId(connection);
            } while (studentDao.findStudentsByGroupId(connection, groupId).size() >= 30);
            result.add(studentDao.save(connection, new Student(groupId, studentFirstName, studentLastName)));
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

    private String randomFirstName(String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    private String randomLastName(String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    private Long randomCourse(Connection connection) throws SQLException {
        return groupDao.findAll(connection).get(ThreadLocalRandom.current().nextInt(groupDao.findAll(connection).size())).getId();
    }

    private Long randomGroupId(Connection connection) throws SQLException {
        return groupDao.findAll(connection).get(ThreadLocalRandom.current().nextInt(groupDao.findAll(connection).size())).getId();
    }

}
