# Personal Finance Tracker

## Project Description

The Personal Finance Tracker is a Java console application that lets users manage their bank accounts and track every dollar going in or out. The app stores all data in a local SQLite database so records survive between sessions. Users can add accounts, record income and expenses with categories, view transaction history, review a spending summary by category, update existing transactions, and delete transactions or entire accounts.

The project was built in three phases: Phase 1 established the class hierarchy using inheritance, composition, and polymorphism. Phase 2 wired in full database persistence using JDBC and SQLite with complete CRUD operations. Phase 3 completed the CRUD surface in the user interface with update and delete options and polished the final product.

## Project Tasks

- **Phase 1: Class Implementation**
  - Designed the class hierarchy: abstract Transaction base class with Income and Expense subclasses
  - Built Account using composition (owns its transaction list) and polymorphism (applyTo at runtime)
  - Added Category class for labeling transactions
  - Built a menu-driven console interface with sample data seeding
- **Phase 2: Database Implementation**
  - Created DatabaseManager using JDBC and SQLite
  - Implemented full CRUD for Categories, Accounts, and Transactions tables
  - Connected startup/shutdown to automatically load and save data
- **Phase 3: Final Submission**
  - Added Update Transaction menu option
  - Added Delete Transaction and Delete Account menu options with confirmation
  - Updated project README and created Phase 3 release tag

## Project Skills Learned

- Object-oriented design with abstract classes, inheritance, composition, and polymorphism
- Java access specifiers and encapsulation
- JDBC database programming with SQLite
- Full CRUD operations in a relational database schema
- Menu-driven console UI design in Java
- Version control and phased project delivery with Git and GitHub

## Language Used

- **Java**: Core application language for all domain classes and the main controller
- **SQLite**: Lightweight embedded relational database accessed via JDBC

## Development Process Used

- **Phased Delivery**: Each phase delivered a complete working slice of functionality, keeping the project runnable at every stage while building toward the full feature set.

## Running the Project

1. Make sure Java 11+ is installed
2. Download the SQLite JDBC driver (sqlite-jdbc-*.jar) and place it in the project folder
3. Compile: `javac -cp sqlite-jdbc-*.jar *.java`
4. Run (Mac/Linux): `java -cp .:sqlite-jdbc-*.jar FinanceTracker`
4. Run (Windows): `java -cp .;sqlite-jdbc-*.jar FinanceTracker`

The database file `patrickg_finance.db` is created automatically on first run and pre-loaded with sample data.

## Video Demo

[Personal Finance Tracker - Demo Video](https://youtu.be/PLACEHOLDER)

## Link to Project

[GitHub Repository](https://github.com/patrickg0526/SDC330_Personal_Finance_Tracker)
