package com.github.mseeger.sql;

import java.sql.PreparedStatement;

public class NoParamsSQLQuery extends SQLQuery {
    private final String queryString;

    NoParamsSQLQuery(String queryString) {
        if (queryString.contains("?"))
            throw new IllegalArgumentException(
                    "queryString='" + queryString + "' must not contain ? slots. "
            );
        this.queryString = queryString;
    }

    @Override
    protected String getQueryString() {
        return queryString;
    }

    @Override
    protected void imputeParameters(PreparedStatement statement) {}
}
