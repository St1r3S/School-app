package com.example.dao;

import com.example.dao.datasource.Datasource;
import com.example.dao.datasource.SimpleDatasource;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.util.Properties;
import java.util.function.Function;

public class EmbeddedPostgresSimpleDatasourceProvider implements Function<EmbeddedPostgres, Datasource> {
    @Override
    public Datasource apply(EmbeddedPostgres database) {
        Properties properties = new Properties();
        properties.setProperty(SimpleDatasource.DRIVER_CLASS, org.postgresql.Driver.class.getName());
        properties.setProperty(SimpleDatasource.JDBC_URL,
                String.format("jdbc:postgresql://localhost:%d/", database.getPort()));
        properties.setProperty(SimpleDatasource.USERNAME, "postgres");
        properties.setProperty(SimpleDatasource.PASSWORD, "postgres");
        return new SimpleDatasource(properties);
    }
}
