package com.github.mseeger.sql;

import java.sql.PreparedStatement;

public class NoParamsSQLQuery extends SQLQuery {
    NoParamsSQLQuery(String queryString) {
        super(queryString);
        if (queryString.contains("?"))
            throw new IllegalArgumentException(
                    "queryString='" + queryString + "' must not contain ? slots. "
            );
    }

    @Override
    protected void imputeParameters(PreparedStatement statement) {}
}