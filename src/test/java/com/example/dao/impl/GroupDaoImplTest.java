package com.example.dao.impl;

import com.example.dao.DaoTestBase;
import com.example.dao.EmbeddedPostgresSimpleDatasourceProvider;
import com.example.dao.GroupDao;
import com.example.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.example.utils.SqlUtils.executeSqlScriptFile;
import static com.example.utils.TransactionUtils.transaction;
import static org.junit.jupiter.api.Assertions.*;


public class GroupDaoImplTest extends DaoTestBase {

    GroupDao dao = new GroupDaoImpl();

    public GroupDaoImplTest() throws IOException {
        super(new EmbeddedPostgresSimpleDatasourceProvider());
    }

    @BeforeEach
    void setUp() throws SQLException {
        executeSqlScriptFile(datasource, "sql/drop_schema.sql");
        executeSqlScriptFile(datasource, "sql/init_schema.sql");
        executeSqlScriptFile(datasource, "test_sql/group_sample.sql");
    }

    @Test
    void shouldCreateGroup() throws SQLException {
        Group initial = new Group(UUID.randomUUID().toString());

        transaction(datasource, connection -> dao.save(connection, initial));

        transaction(datasource, connection -> {
            long count = dao.findAll(connection).stream().filter(it ->
                    it.getName().equals(initial.getName())
            ).count();

            assertEquals(1, count);
        });
    }

    @Test
    void shouldUpdateGroup() throws SQLException {
        String updatedName = "i updated you! 42";
        transaction(datasource, connection -> {
            Group initial = dao.findById(connection, 30000L).orElseThrow();
            assertNotEquals(updatedName, initial.getName());
            Group toSave = new Group(initial.getId(), updatedName);
            dao.save(connection, toSave);
        });

        transaction(datasource, connection -> {
            Group updated = dao.findById(connection, 30000L).orElseThrow();
            assertEquals(30000, updated.getId());
            assertEquals(updatedName, updated.getName());
        });
    }

    @Test
    void shouldFindById() throws SQLException {
        transaction(datasource, (connection -> {
            Group actual = dao.findById(connection, 10000L).orElseThrow();
            Group expected = new Group(10000L, "find me!");
            assertEquals(expected, actual);
        }));
    }

    @Test
    void shouldFindByName() throws SQLException {
        transaction(datasource, (connection -> {
            Group actual = dao.findByName(connection, "find me!").orElseThrow();
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
    void shouldFindGroupsByStudentsAmount() throws SQLException {
        transaction(datasource, (connection -> {
            List<Group> actual = dao.findAllByStudentsAmount(connection, 1);
            assertEquals(10000, actual.get(0).getId());
        }));
    }

}