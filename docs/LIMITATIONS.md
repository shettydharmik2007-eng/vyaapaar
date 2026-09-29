# Project Limitations

The **Vyaapaar** system was developed as a clean, college-level demonstration of Core Java, JDBC, and relational database management. The following genuine architectural constraints and limitations apply to the current version:

---

### 1. Console / CLI-Based User Interface
* The user interface is text-driven via standard terminal prompts and ASCII tables.
* It lacks a graphical user interface (GUI) or browser-based frontend (e.g., HTML/CSS/JavaScript or JavaFX).

### 2. Local Desktop Execution
* The system is designed to run locally on a single machine connecting to `localhost:3306` (MySQL) and `localhost:5432` (PostgreSQL).
* It does not currently utilize cloud databases (e.g., AWS RDS or Google Cloud SQL) or containerized orchestration (Docker).

### 3. Simulated Payment Gateway
* Checkout confirms orders immediately upon delivery address verification.
* There is no integration with real-world payment gateways (such as Razorpay, Stripe, or PayPal) or banking APIs.

### 4. Basic Plaintext Password Storage
* To keep the code simple, understandable, and focused on SQL/JDBC for academic review, password hashing algorithms (such as BCrypt or PBKDF2) are not implemented.

### 5. Independent Reporting Database Population
* The PostgreSQL reporting database runs on its own relational schema and is populated using dedicated SQL scripts rather than a real-time Change Data Capture (CDC) or ETL streaming pipeline.
