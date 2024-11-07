# Notes and Links about Maven in IntelliJ

Useful links:

* [Crash Course Maven (Video)](https://www.youtube.com/watch?v=Xatr8AZLOsE)
* [Maven in IntelliJ](https://www.jetbrains.com/guide/java/tutorials/working-with-maven/)
* [Maven Getting Started](https://maven.apache.org/guides/getting-started/index.html)

Notes:

* Create Maven wrapper in repo, so Maven does not have to be installed:
  ```bash
  mvn wrapper:wrapper
  ```
* Maven stages (coomands):
  ```bash
  mvn clean     # Remove target
  mvn compile   # Compile all classes under src/main/java
  mvn test      # Compile and run tests under src/test/java
  mvn package   # compile, test, create single JAR in target/
  mvn install   # package, put JAR into local mvn repo at ~/.m2/repository
  ```
* Maven structure:
  ```bash
  pom.xml              # Build configuration (dependencies, ...)
  src/main/java/       # Source code
  src/test/java/       # Test code (same structure as source)
  src/main/resources   # Extra files, not compiled but deployed
  src/test/resources   # Resources for test, not compiled but deployed
  ```
* Add dependencies in `pom.xml` in `<dependency>` tag. In IDEA use
  `Code -> Generate -> Dependency`.
