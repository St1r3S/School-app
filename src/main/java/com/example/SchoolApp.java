package com.example;

import com.example.dao.datasource.Datasource;
import com.example.dao.datasource.SimpleDatasource;
import com.example.dao.impl.CourseDaoImpl;
import com.example.dao.impl.GroupDaoImpl;
import com.example.dao.impl.StudentDaoImpl;
import com.example.misc.Generator;
import com.example.model.Course;
import com.example.model.Student;
import com.example.utils.SqlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.example.misc.Generator.courses;
import static com.example.utils.ResourceUtils.loadPropertiesFromResources;
import static com.example.utils.TransactionUtils.transaction;

public class SchoolApp implements Closeable {

    private final Datasource datasource;
    private final GroupDaoImpl groupDao;
    private final StudentDaoImpl studentDao;
    private final CourseDaoImpl courseDao;

    private static final String HEADER = "\nType letter of function to execute it. To end the program type \"end\".\n\n" +
            "a. Find all groups with less or equals student count\n" +
            "b. Find all students related to course with given name\n" +
            "c. Add new student\n" +
            "d. Delete student by STUDENT_ID\n" +
            "e. Add a student to the course (from a list)\n" +
            "f. Remove the student from one of his or her courses\n\n" +
            "Your input -> ";

    public SchoolApp(Datasource datasource) throws SQLException {
        this.datasource = datasource;

        // setup database
        SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");

        // setup dao components
        this.groupDao = new GroupDaoImpl();
        this.studentDao = new StudentDaoImpl();
        this.courseDao = new CourseDaoImpl();
    }

    private void run(String choice) throws SQLException {
        // fill db with generated data
        System.out.println("Generating data...");
        transaction(datasource, (connection -> new Generator(groupDao, studentDao, courseDao).generateData(connection, 10, 200)));
        System.out.println("Data successfully generated");
        // show menu in a loop
        Scanner choose = new Scanner(System.in);
        int amount;
        while (!"end".equals(choice)) {
            System.out.print(HEADER);
            choice = choose.nextLine();
            if ("a".equals(choice)) {
                System.out.print("\nEnter student amount(up to 30). To end the program type \"end\".\nYour input -> ");
                amount = choose.nextInt();
                choose.nextLine();
                System.out.println(groupDao.findAllByStudentsAmount(datasource.getConnection(), amount));
                choice = null;
            }
            if ("b".equals(choice)) {
                System.out.print("\nEnter course name. To end the program type \"end\".\nYour input -> ");
                choice = choose.nextLine();
                List<Student> students = studentDao.findByCourseName(datasource.getConnection(), choice);
                if (!students.isEmpty()) {
                    System.out.println(students);
                } else {
                    System.out.println("This course is not attended by anyone.");
                }
                choice = null;
            }
            if ("c".equals(choice)) {
                System.out.print("\nEnter group id. To end the program type \"end\".\nYour input -> ");
                Long groupId = choose.nextLong();
                choose.nextLine();
                System.out.print("\nEnter the student's first name. To end the program type \"end\".\nYour input -> ");
                String firstName = choose.nextLine();
                System.out.print("\nEnter the student's last name. To end the program type \"end\".\nYour input -> ");
                String lastName = choose.nextLine();
                Student student = new Student(groupId, firstName, lastName);
                studentDao.create(datasource.getConnection(), student);
                System.out.print("\nStudent was added successfully!");
                choice = null;
            }
            if ("d".equals(choice)) {
                System.out.print("\nEnter student's id, to delete that exact student. To end the program type \"end\".\nYour input -> ");
                Long studentId = choose.nextLong();
                choose.nextLine();
                studentDao.deleteById(datasource.getConnection(), studentId);
                choice = null;
            }
            if ("e".equals(choice)) {
                System.out.print("\nRead course list to which you'll add student.");
                int[] iArr = {1};
                Arrays.stream(courses).forEach(e -> System.out.print("\n" + iArr[0]++ + ". " + e + "."));
                System.out.print("\nEnter course number, to which you'll add student. To end the program type \"end\".\nYour input -> ");
                Long courseId = choose.nextLong();
                choose.nextLine();
                System.out.print("\nEnter student's id, whose you'll add to course entered before. To end the program type \"end\".\nYour input -> ");
                Long studentId = choose.nextLong();
                studentDao.enroll(datasource.getConnection(), studentId, courseId);
                System.out.print("\nStudent was successfully added to course.");
                choice = null;
            }
            if ("f".equals(choice)) {
                System.out.print("\nEnter student's id, to delete him/her from courses below. To end the program type \"end\".\nYour input -> ");
                Long studentId = choose.nextLong();
                choose.nextLine();
                System.out.print("\nRead course list from which you can remove student.");
                List<Course> currentCourses = courseDao.findByStudentId(datasource.getConnection(), studentId);
                String[] studentCourses = Arrays.stream(courses).filter(e -> currentCourses.stream().map(c -> c.getName()).collect(Collectors.toList()).contains(e)).toArray(String[]::new);
                int[] iArr = {1};
                Arrays.stream(studentCourses).forEach(e -> System.out.print("\n" + iArr[0]++ + ". " + e + "."));
                System.out.print("\nEnter course number, from which you'll remove student. To end the program type \"end\".\nYour input -> ");
                int courseId = choose.nextInt();
                choose.nextLine();
                studentDao.expel(datasource.getConnection(), studentId, currentCourses.stream().filter(e -> e.getName().equals(studentCourses[courseId - 1])).findFirst().get().getId());
                System.out.print("\nStudent was successfully removed from course.");
                choice = null;
            }
        }
        choose.close();
    }


    @Override
    public void close() throws IOException {
        try {
            SqlUtils.executeSqlScriptFile(datasource, "sql/drop_schema.sql");
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        Properties databaseProperties = loadPropertiesFromResources("db.properties");
        try (
                Datasource datasource = new SimpleDatasource(databaseProperties);
                SchoolApp schoolApp = new SchoolApp(datasource)
        ) {
            schoolApp.run(null);
        }
    }

}
