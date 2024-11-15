package com.github.mseeger.sql;

import java.util.Properties;

public class ConnectionConfig {
    public static String defaultDriverName = "jdbc:mysql";
    public static String defaultHost = "localhost";
    public static int defaultPort = 3306;

    private final String user;
    private final String password;
    private final String database;
    private final int port;
    private final String host;
    private final String driverName;

    ConnectionConfig(
            String user,
            String password,
            String database,
            int port,
            String host,
            String driverName
    ) {
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
        this.host = host;
        this.driverName = driverName;
    }

    ConnectionConfig(
            String user,
            String password,
            String database,
            int port,
            String host
    ) {
        this(user, password, database, port, host, defaultDriverName);
    }

    ConnectionConfig(
            String user,
            String password,
            String database,
            int port
    ) {
        this(user, password, database, port, defaultHost, defaultDriverName);
    }

    ConnectionConfig(
            String user,
            String password,
            String database
    ) {
        this(user, password, database, defaultPort, defaultHost, defaultDriverName);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getURL() {
        return String.format("%s://%s:%d/%s", driverName, host, port, database);
    }

    public Properties getProperties() {
        var properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        return properties;
    }
}