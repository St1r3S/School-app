package com.example.dao.impl;

import com.example.dao.DaoTestBase;
import com.example.dao.EmbeddedPostgresSimpleDatasourceProvider;
import com.example.dao.StudentDao;
import com.example.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.example.utils.SqlUtils.executeSqlScriptFile;
import static com.example.utils.TransactionUtils.transaction;
import static org.junit.jupiter.api.Assertions.*;


public class StudentDaoImplTest extends DaoTestBase {

    StudentDao dao = new StudentDaoImpl();

    public StudentDaoImplTest() throws IOException {
        super(new EmbeddedPostgresSimpleDatasourceProvider());
    }

    @BeforeEach
    void setUp() throws SQLException {
        executeSqlScriptFile(datasource, "sql/drop_schema.sql");
        executeSqlScriptFile(datasource, "sql/init_schema.sql");
        executeSqlScriptFile(datasource, "test_sql/group_sample.sql");
        executeSqlScriptFile(datasource, "test_sql/student_sample.sql");
    }

    @Test
    void shouldCreateStudent() throws SQLException {
        Student initial = new Student(10000L, UUID.randomUUID().toString(), UUID.randomUUID().toString());

        transaction(datasource, connection -> dao.save(connection, initial));

        transaction(datasource, connection -> {
            long count = dao.findAll(connection).stream().filter(it ->
                    it.getFirstName().equals(initial.getFirstName())
            ).count();

            assertEquals(1, count);
        });
    }

    @Test
    void shouldUpdateStudent() throws SQLException {
        String updatedFirstName = "i updated you! 42";
        String updatedLastName = "i updated you! 42";
        transaction(datasource, connection -> {
            Student initial = dao.findById(connection, 30000L).orElseThrow();
            assertNotEquals(updatedFirstName, initial.getFirstName());
            Student toSave = new Student(initial.getId(), initial.getGroupId(), updatedFirstName, updatedLastName);
            dao.save(connection, toSave);
        });

        transaction(datasource, connection -> {
            Student updated = dao.findById(connection, 30000L).orElseThrow();
            assertEquals(30000, updated.getId());
            assertEquals(updatedFirstName, updated.getFirstName());
            assertEquals(updatedLastName, updated.getLastName());
        });
    }

    @Test
    void shouldFindById() throws SQLException {
        transaction(datasource, (connection -> {
            Student actual = dao.findById(connection, 10000L).orElseThrow();
            Student expected = new Student(10000L, 10000L, "find me!", "lebovsky");
            assertEquals(expected, actual);
        }));
    }

    @Test
    void shouldFindByCourseId() throws SQLException {
        executeSqlScriptFile(datasource, "test_sql/course_sample.sql");
        executeSqlScriptFile(datasource, "test_sql/students_courses_sample.sql");
        transaction(datasource, (connection -> {
            List<Student> actual = dao.findByCourseId(connection, 10000L);
            Student expected = new Student(10000L, 10000L, "find me!", "lebovsky");
            assertEquals(expected, actual.get(0));
        }));
    }

    @Test
    void shouldFindByGroupId() throws SQLException {
        transaction(datasource, (connection -> {
            List<Student> actual = dao.findStudentsByGroupId(connection, 10000L);
            Student expected = new Student(10000L, 10000L, "find me!", "lebovsky");
            assertEquals(expected, actual.get(0));
        }));
    }

    @Test
    void shouldFindByName() throws SQLException {
        transaction(datasource, (connection -> {
            Student actual = dao.findByFirstNameAndLastName(connection, "find me!", "lebovsky").orElseThrow();
            assertEquals(10000, actual.getId());
        }));
    }

    @Test
    void shouldNotFindById() throws SQLException {
        transaction(datasource, (connection -> assertFalse(dao.findById(connection, 100000L).isPresent())));
    }

    @Test
    void shouldDelete() throws SQLException {
        transaction(datasource, (connection -> {
            dao.deleteById(connection, 20000L);
            assertFalse(dao.findById(connection, 20000L).isPresent());
        }));
    }

    @Test
    void shouldNotDelete() throws SQLException {
        transaction(datasource, (connection ->
                assertThrows(SQLException.class, () -> dao.deleteById(connection, 21000L))));
    }

    @Test
    void shouldEnrollStudent() throws SQLException {
        executeSqlScriptFile(datasource, "test_sql/course_sample.sql");
        transaction(datasource, (connection -> {
            dao.enroll(connection, 10000L, 10000L);
            List<Student> actual = dao.findByCourseId(connection, 10000L);
            assertEquals(10000L, actual.get(0).getId());
        }));
    }

    @Test
    void shouldExpelStudent() throws SQLException {
        executeSqlScriptFile(datasource, "test_sql/course_sample.sql");
        executeSqlScriptFile(datasource, "test_sql/students_courses_sample.sql");
        transaction(datasource, (connection -> {
            dao.enroll(connection, 10000L, 10000L);
            List<Student> actual = dao.findByCourseId(connection, 10000L);
            assertFalse(actual.isEmpty());
        }));
    }
}