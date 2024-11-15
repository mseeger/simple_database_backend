package com.github.mseeger.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SQLQuery {
    protected abstract String getQueryString();

    /**
     * If the query string contains slots "?", this method imputes values for
     * them.
     *
     * @param statement Imputes values for free slots in this statement
     */
    protected abstract void imputeParameters(PreparedStatement statement) throws SQLException;

    /**
     * Creates prepared statement from query string and imputes values for
     * free slots.
     *
     * @param connection Connection
     */
    public PreparedStatement getStatement(Connection connection) throws SQLException {
        var statement = connection.prepareStatement(getQueryString());
        imputeParameters(statement);
        return statement;
    }
}
