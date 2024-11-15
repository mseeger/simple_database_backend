package com.github.mseeger.sql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor<T> {
    private final ConnectionConfig connectionConfig;

    public QueryExecutor(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    /**
     * Executes SQL query and returns result set as list of `T`.
     *
     * @param query SQL query
     * @param rowMapper Maps result set rows to entity objects of type `T`
     * @return Result list of entity objects
     * @throws SQLException
     */
    public List<T> run(SQLQuery query, RowMapper<T> rowMapper) throws SQLException {
        ArrayList<T> resultList;
        try (
                var connection = DriverManager.getConnection(
                        connectionConfig.getURL(), connectionConfig.getProperties()
                );
                var statement = query.getStatement(connection);
                var resultSet = statement.executeQuery()
        ) {
            resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(rowMapper.map(resultSet));
            }
        }
        return resultList;
    }
}
