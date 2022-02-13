package com.example;

import com.example.dao.datasource.Datasource;
import com.example.dao.datasource.SimpleDatasource;
import com.example.dao.impl.GroupDaoImpl;
import com.example.misc.Generator;
import com.example.utils.SqlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static com.example.utils.ResourceUtils.loadPropertiesFromResources;
import static com.example.utils.TransactionUtils.transaction;

public class SchoolApp implements Closeable {

    private final Datasource datasource;
    private final GroupDaoImpl groupDao;

    public SchoolApp(Datasource datasource) throws SQLException {
        this.datasource = datasource;

        // setup database
        SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");

        // setup dao components
        this.groupDao = new GroupDaoImpl();
    }

    private void run() throws SQLException {
        // fill db with generated data
        transaction(datasource, (connection -> new Generator(groupDao).generateData(connection, 10)));

        // show menu in a loop
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
            schoolApp.run();
        }
    }
}
