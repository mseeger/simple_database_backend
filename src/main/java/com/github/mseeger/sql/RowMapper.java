package com.github.mseeger.sql;

import java.sql.ResultSet;

public interface RowMapper<T> {
    /**
     * @param rs Current result set row taken from here
     * @return Entity object corresponding to row
     */
    T map(ResultSet rs);
}
