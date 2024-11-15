package com.github.mseeger.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    /**
     * @param rs Current result set row taken from here
     * @return Entity object corresponding to row
     */
    T map(ResultSet rs) throws SQLException;
}
