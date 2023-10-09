package com.terryliu.springbootmultipledatasources.db;

import com.pgvector.PGvector;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description custom datasource for postgresql
 */
@Slf4j
public class PgVectorDataSource extends HikariDataSource {
    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        setVectorType(connection);
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        setVectorType(connection);
        return connection;
    }

    /**
     * add vector type to connection
     *
     * @param connection database connection instance
     */
    private void setVectorType(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            PGvector.addVectorType(connection);
        } catch (Throwable t) {
            //ignore exception
            log.error("Set vector type error:", t);
        }
    }
}
