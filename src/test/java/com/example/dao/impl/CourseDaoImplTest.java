package com.example.dao.impl;

import com.example.dao.CourseDao;
import com.example.dao.DaoTestBase;
import com.example.dao.EmbeddedPostgresSimpleDatasourceProvider;
import com.example.model.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import static com.example.utils.SqlUtils.executeSqlScriptFile;
import static com.example.utils.TransactionUtils.transaction;
import static org.junit.jupiter.api.Assertions.*;


public class CourseDaoImplTest extends DaoTestBase {

    CourseDao dao = new CourseDaoImpl();

    public CourseDaoImplTest() throws IOException {
        super(new EmbeddedPostgresSimpleDatasourceProvider());
    }

    @BeforeEach
    void setUp() throws SQLException {
        executeSqlScriptFile(datasource, "sql/drop_schema.sql");
        executeSqlScriptFile(datasource, "sql/init_schema.sql");
        executeSqlScriptFile(datasource, "test_sql/course_sample.sql");
    }

    @Test
    void shouldCreateCourse() throws SQLException {
        Course initial = new Course(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        transaction(datasource, connection -> dao.save(connection, initial));

        transaction(datasource, connection -> {
            long count = dao.findAll(connection).stream().filter(it ->
                    it.getName().equals(initial.getName())
            ).count();

            assertEquals(1, count);
        });
    }

    @Test
    void shouldUpdateCourse() throws SQLException {
        String updatedName = "i updated you! 42";
        transaction(datasource, connection -> {
            Course initial = dao.findById(connection, 30000L).orElseThrow();
            assertNotEquals(updatedName, initial.getName());
            Course toSave = new Course(initial.getId(), updatedName, initial.getDescription());
            dao.save(connection, toSave);
        });

        transaction(datasource, connection -> {
            Course updated = dao.findById(connection, 30000L).orElseThrow();
            assertEquals(30000, updated.getId());
            assertEquals(updatedName, updated.getName());
        });
    }

    @Test
    void shouldFindById() throws SQLException {
        transaction(datasource, (connection -> {
            Course actual = dao.findById(connection, 10000L).orElseThrow();
            Course expected = new Course(10000L, "find me!", "for find");
            assertEquals(expected, actual);
        }));
    }

    @Test
    void shouldFindByName() throws SQLException {
        transaction(datasource, (connection -> {
            Course actual = dao.findByName(connection, "find me!").orElseThrow();
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

}