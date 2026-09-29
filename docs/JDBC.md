# Java Database Connectivity (JDBC) Documentation

## 1. What is JDBC?
**Java Database Connectivity (JDBC)** is a standard Java API that enables Java applications to interact with relational database management systems (RDBMS) via SQL queries. In **Vyaapaar**, raw JDBC is utilized across all Data Access Objects (DAOs) without relying on high-level ORM frameworks like Hibernate.

---

## 2. Core JDBC Components in Vyaapaar

### A. JDBC Drivers
* **MySQL Connector/J (`com.mysql.cj.jdbc.Driver`)**: Bridges Java and the primary MySQL database.
* **PostgreSQL Driver (`org.postgresql.Driver`)**: Bridges Java and the PostgreSQL reporting database.
* Drivers are declared as Maven dependencies in `pom.xml` and loaded cleanly via `Class.forName()`.

### B. `java.sql.Connection`
Represents a physical connection session with the database.
* Managed centrally via [`DatabaseConnection.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/config/DatabaseConnection.java) (MySQL) and [`PostgreSQLConnection.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/config/PostgreSQLConnection.java) (PostgreSQL).
* Credentials are loaded dynamically from `db.properties`.

### C. `java.sql.PreparedStatement`
Pre-compiles SQL statements on the database server and safely binds parameter values:
* **SQL Injection Prevention**: Escapes special characters automatically.
* **Performance**: Pre-compiled query execution plans.
* **Type Safety**: Explicit setter methods (`stmt.setString(1, ...)`, `stmt.setInt(2, ...)`, `stmt.setDouble(3, ...)`).

### D. `java.sql.ResultSet`
A tabular data stream returned by `executeQuery()`.
* Iterated using `while (rs.next())` to map relational columns into domain model POJOs.

### E. `java.sql.SQLException`
Base exception for database access errors, syntax issues, or constraint violations.
* Handled within DAOs and encapsulated into descriptive messages for the Service layer.

---

## 3. Resource Management via `try-with-resources`

All JDBC resources (`Connection`, `PreparedStatement`, `ResultSet`) implement `java.lang.AutoCloseable`. **Vyaapaar** uses `try-with-resources` blocks everywhere to guarantee that connections and statements are immediately closed upon block exit, preventing database connection leaks.

```java
// Example from UserDao.java
public User findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, email);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error finding user by email: " + e.getMessage());
    }
    return null;
}
```

---

## 4. JDBC Transaction Management

By default, JDBC operates in **auto-commit mode**, meaning every single SQL statement is committed immediately upon execution.

For multi-step business transactions (such as Checkout), Vyaapaar disables auto-commit to create an explicit ACID transaction boundary:
1. `conn.setAutoCommit(false);` $\rightarrow$ Disables automatic persistence.
2. Executes Order creation, item snapshots, inventory deductions, and cart clearing.
3. `conn.commit();` $\rightarrow$ Permanently applies all changes together.
4. `conn.rollback();` $\rightarrow$ Discards all pending modifications if any exception occurs.
5. `conn.setAutoCommit(true);` $\rightarrow$ Restores default connection state in a `finally` block before closing.
