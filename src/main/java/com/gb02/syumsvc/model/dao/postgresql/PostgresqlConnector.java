package com.gb02.syumsvc.model.dao.postgresql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostgresqlConnector {
    
    private static DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource ds) {
        PostgresqlConnector.dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }
        return dataSource.getConnection();
    }

}
