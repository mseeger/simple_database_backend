package com.github.mseeger.sql.queries;

import com.github.mseeger.sql.QueryExecutor;
import com.github.mseeger.sql.RowMapper;
import com.github.mseeger.sql.SQLQuery;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FilmsInStockQuery {
    private static final String queryString = """
            WITH
            instock_inventory AS (
            SELECT DISTINCT ia.inventory_id
              FROM inventory AS ia
              LEFT JOIN (SELECT inventory_id
                           FROM rental
                          WHERE rental_date <= ?
                            AND (return_date IS NULL OR return_date > ?)
                            AND store_id = ?) AS ir
                ON ia.inventory_id = ir.inventory_id
             WHERE ia.store_id = ? AND ir.inventory_id IS NULL
            )
            
            SELECT f.film_id, f.title, COUNT(*)
              FROM inventory AS i
              JOIN instock_inventory AS ii
                ON i.inventory_id = ii.inventory_id
              JOIN film AS f
                ON i.film_id = f.film_id
             GROUP BY f.film_id
             ORDER BY f.film_id
            """;
    private static final int[] storeIDIndices = {3};
    private static final int[] referenceDateTimeIndices = {1, 2};
    private static final RowMapper<FilmsInStock> rowMapper = resultSet ->
    {
        int filmID = resultSet.getInt(1);
        String title = resultSet.getString(2);
        int numInStock = resultSet.getInt(3);
        return new FilmsInStock(filmID, title, numInStock);
    };

    private final QueryExecutor<FilmsInStock> queryExecutor;

    public FilmsInStockQuery(QueryExecutor<FilmsInStock> queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * Returns records for all films which are in stock in a store at a reference
     * time point.
     *
     * @param storeID ID of store
     * @param referenceDateTime Reference time point
     * @return List of records
     */
    public ArrayList<FilmsInStock> run(int storeID, LocalDateTime referenceDateTime) throws SQLException {
        class Query extends SQLQuery {
            protected String getQueryString() {
                return queryString;
            }

            protected void imputeParameters(PreparedStatement statement) throws SQLException {
                for (int index : storeIDIndices) {
                    statement.setInt(index, storeID);
                }
                var refTimeStamp = Timestamp.valueOf(referenceDateTime);
                for (int index : referenceDateTimeIndices) {
                    statement.setTimestamp(index, refTimeStamp);
                }
            }
        }

        return queryExecutor.run(new Query(), rowMapper);
    }
}