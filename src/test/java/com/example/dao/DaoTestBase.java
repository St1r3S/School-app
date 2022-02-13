package com.example.dao;

import com.example.dao.datasource.Datasource;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;
import java.util.function.Function;

public class DaoTestBase {

    private static EmbeddedPostgres database = null;
    protected Datasource datasource;

    public DaoTestBase(Function<EmbeddedPostgres, Datasource> datasourceProvider) throws IOException {
        if (database == null) {
            synchronized (this) {
                if (datasource == null) {
                    database = EmbeddedPostgres.builder().start();
                }
            }
        }
        datasource = datasourceProvider.apply(database);
    }
}
