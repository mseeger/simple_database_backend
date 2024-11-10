# Notes and Links about MySQL

Useful links:

* [Tutorial (Ref Manual 8.4)](https://dev.mysql.com/doc/refman/8.4/en/tutorial.html)

## Installation

I used Homebrew:
```bash
brew install mysql
```
Installed to `/opt/homebrew/opt/mysql`, `Ver 9.0.1 for macos13.7 on arm64 (Homebrew)`.
Output from brew:
```text
Upgrading from MySQL <8.4 to MySQL >9.0 requires running MySQL 8.4 first:
 - brew services stop mysql
 - brew install mysql@8.4
 - brew services start mysql@8.4
 - brew services stop mysql@8.4
 - brew services start mysql

We've installed your MySQL database without a root password. To secure it run:
    mysql_secure_installation

MySQL is configured to only allow connections from localhost by default

To connect run:
    mysql -u root

To start mysql now and restart at login:
  brew services start mysql
Or, if you don't want/need a background service you can just run:
  /opt/homebrew/opt/mysql/bin/mysqld_safe --datadir\=/opt/homebrew/var/mysql
```

Change root password:
```bash
brew services start mysql
mysql_secure_installation
```

Test with:
```bash
mysql -u root -p
```
Note that `mysql -u root` should fail, because `root` has a password now.

Relevant paths:

* Data directory: `datadir = /opt/homebrew/var/mysql`
* Where MySQL is installed: `basedir = /opt/homebrew/Cellar/mysql/9.0.1_6/`

Some tests:
```bash
mysqladmin version -p
mysqladmin variables -p
mysqlshow -p
```

Here, the root password has to be entered in each case.

Notes on installing further components, such as MySQL Workbench and Python `mysql-connector`
are [here](https://gist.github.com/Foadsf/b351fe7686de19a4c91d3e0b4c91080a).

## Accounts and Privileges

An account is specified by a user name and a host name. In our case, the latter
is always `localhost`. A user can be given many different privileges:

* Administrative privileges: Manage operations of the server. Global, not specified
  to a particular database.
* Database privileges: Apply to a database and all objects within it. They can also
  be granted globally, to all databases.
* Database object privileges (tables indexes, views, stored routines): These apply
  objects of a particular type within a database, or to objects of a particular
  type across all databases.

We keep it simple:
* Only `root` has administrative privileges. It creates all accounts and grants
  privileges. It also creates all databases.
* For each database `xyz`, we have accounts `xyz_admin`, `xyz_basic`, `xyz_readonly`.
* We could also create roles, and then assign roles to accounts, but this seems
  too complex for now.

For now, we assign these privileges. This may change as we learn more:

```sql
GRANT SELECT
   ON xyz.*
   TO 'xyz_readonly'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE TEMPORARY TABLES
   ON xyz.*
   TO 'xyz_basic'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE TEMPORARY TABLES,
      CREATE, ALTER, INDEX, DROP, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE,
      EXECUTE, CREATE VIEW, EVENT, TRIGGER, FILE
   ON xyz.*
   TO  'xyz_admin'@'localhost';
```

If database `xyz` is accessed by an App, this would use `xyz_readonly` or
`xyz_basic`.

### Useful Commands

Show all accounts. As admin (root) user:
```sql
SELECT USER, HOST FROM mysql.user;
```

Show privileges of user:
```sql
show grants for <user>;
```

## Importing Data

By default, MySQL is configured to not allow `LOAD DATA` from anywhere. This
makes sense for security reasons. In order to allow importing data from files
in `<dir>`, start `mysql` like this:

```bash
mysql -u root -p --load-data-local-dir=<dir> <databasename>
```

In order to import data from a CSV file, first create the table using the column
names from the file, then import data with:

```sql
LOAD DATA INFILE <filename>
INTO TABLE <tbl_name>
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES;
```

There is also `mysqlimport`, which may be more convenient. However, this fails if
`LOAD DATA` is not allowed. We can allow `LOAD DATA` from anywhere by starting
the server with the `--local-infile` option.


## Sample Databases

Here, we describe a number of sample databases for MySQL.

### Sakila

Details are [here](https://dev.mysql.com/doc/sakila/en/).

The Sakila sample database is designed to represent a DVD rental store. After
the basic schema was completed, various views, stored routines, and triggers were
added to the schema; then the sample data was populated.

Interesting tables:

* `payment` joins `customer`, `rental` (optional), `staff`, with fields `amount`
  and `payment_date`
* `rental` joins `customer`, `inventory`, `staff`
* `address` contains postal addresses used in `stores`, `customers`, `staff`
* `film_category` joins `film`, `category`
* `film` contains information about films. Each film seems to have a unique
  `rental_duration`
* `film_actor` joins `film`, `actor`
* `inventory` joins `film`, `store`. An inventory item is in stock iff it does
  not appear in `rental`
* `film_text` contains the `film_id`, `title` and `description` columns of the
  `film` table, with the contents of the table kept in synchrony with the `film`
  table by means of triggers on `film` table INSERT, UPDATE and DELETE operations.
  Should not be modified directly.

Find out / sanity check:

* Are rows in `rental` removed when inventory is returned, or is
  `rental.return_date` set? The latter makes it a bit harder to check
  inventory for in stock.
* When are rentals paid, as entry in `payment`? Does this correspond to
  `film.rental_period`, `film.rental_rate`?

Interesting queries:

* For a store: Which inventory items are in stock, and how often? Include
  relevant attributes to be shown to a customer.
* For a customer: Which inventory items do they currently rent, and when is
  each due? Are any items past their return date? What are charges for late
  returns (assuming these are determined by `film.rental_duration` and
  `film.rental_rate`)?
* For a staff member and time period: What is the sum of rental rates for
  inventory in rentals processed by this member? This is quite different from
  the `sales_by_store` view.
* List name and complete address (including city, country) for all customers.
  This is provided by `customer_list` view.
* Display complete information for films. This is provided by `film_list` view.
  Can be used for a detail page.
* For a customer: What are all films they ever rented? For each film, list the
  total rental time in days and total rental costs, as well as category and
  other relevant attributes. This information can be used for recommendation.

Interesting updates:

* Customer is registered with the company: Create row in `customer`, as well
  as other tables like `address`. Only registered customers can rent items.
* Customer rents film in a store: Create row in `rental`. Need to find an
  inventory item in stock. We can assume that the customer pays immediately,
  inserting a row in `payment` as well.
* Customer returns inventory in a store: Update `rental.return_date`. For late
  returns, could charge fees, which leads to new rows in `payment`.
* Add inventory item in some store. This could be for an existing film, or for
  a new one. In the latter case, attributes need to be inserted into other
  tables.
* A staff member is hired.
* A staff member leaves; a staff members transfers from one store to another.

An advanced feature would be approximate matches for entries such as address
(customer, staff member) or title (film). This could also be done as clean-up
operations, which merge rows in the respective tables.

Finally, a really advanced extra idea would be to use Gen AI in order to create
posters for films, depending on their attributes (description, category,
actors). Note that films and actor names are made up. We could 