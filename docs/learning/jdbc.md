# Notes and Links about JDBC

Here is the official [tutorial](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html).

The Maven dependency for MySQL JDBC access is [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/).

## Get a Connection

There are several ways to establish a connection to a MySQL database:
* Use `java.sql.DriverManager`: This is simple, but does not allow for connection
  pooling, and has other disadvantages. It suffices for local access, though.
* Use an application server (such as Tomcat or Jetty) which provides a connection
  pool. This server is accessed through the Java Naming and Directory Interface
  (JNDI), a lookup to which results in a `javax.sql.DataSource` object, which can
  be queried for connections. Here is an
  [example](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-j2ee-concepts-connection-pooling.html).
  The application can be configured to provide a connection pool, which ensures
  that connections to the database are reused.

Setting up an application server just for the local prototype seems overkill.
On the other hand, would like to learn how to do it properly. On the other hand,
this will likely all be simpler when using `Spring`, see this [guide](https://spring.io/guides/gs/accessing-data-mysql).

