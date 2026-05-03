/******************************************************************
 * Name: Patrick Gonzalez
 * Date: 05/03/2026
 * Assignment: SDC330 Course Project - Database Implementation (Phase 2)
 *
 * Service class that handles all JDBC/SQLite interaction for the
 * Personal Finance Tracker. Manages three tables: Categories,
 * Accounts, and Transactions. On connect, all three tables are
 * created if they do not already exist. Every CRUD method uses
 * PreparedStatements and returns the generated database ID where
 * applicable so callers can keep their in-memory objects in sync
 * with the database.
 ******************************************************************/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_NAME = "patrickg_finance.db";
    private String connectionUrl;
    private Connection connection;

    public DatabaseManager() {
        this.connectionUrl = "jdbc:sqlite:" + DB_NAME;
        this.connection = null;
    }

    // Opens a connection to the SQLite database, creating the file
    // if it doesn't exist, then ensures all three tables exist.
    public void connect() {
        try {
            connection = DriverManager.getConnection(connectionUrl);
            createTables();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // Closes the database connection if one is open.
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }

    // Creates the Categories, Accounts, and Transactions tables if
    // they do not already exist. Called automatically on connect.
    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Categories ("
          + "    ID   INTEGER PRIMARY KEY AUTOINCREMENT"
          + "   ,Name VARCHAR(50)  NOT NULL"
          + "   ,Type VARCHAR(20)  NOT NULL);"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Accounts ("
          + "    ID             INTEGER PRIMARY KEY AUTOINCREMENT"
          + "   ,Name           VARCHAR(100) NOT NULL"
          + "   ,Type           VARCHAR(50)  NOT NULL"
          + "   ,OpeningBalance REAL         NOT NULL DEFAULT 0.0);"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Transactions ("
          + "    ID          INTEGER PRIMARY KEY AUTOINCREMENT"
          + "   ,AccountID   INTEGER NOT NULL"
          + "   ,CategoryID  INTEGER NOT NULL"
          + "   ,Amount      REAL    NOT NULL"
          + "   ,Date        TEXT    NOT NULL"
          + "   ,Description VARCHAR(200)"
          + "   ,Type        VARCHAR(10) NOT NULL"
          + "   ,FOREIGN KEY (AccountID)  REFERENCES Accounts(ID)"
          + "   ,FOREIGN KEY (CategoryID) REFERENCES Categories(ID));"
        );

        stmt.close();
    }

    // -------------------------------------------------------
    // Categories
    // -------------------------------------------------------

    // Inserts a new category and returns the generated ID, or
    // -1 if the insert fails.
    public int saveCategory(Category c) {
        String sql = "INSERT INTO Categories(Name, Type) VALUES(?,?)";
        try {
            PreparedStatement pst =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, c.getName());
            pst.setString(2, c.getType());
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("saveCategory error: " + e.getMessage());
        }
        return -1;
    }

    // Updates the name and type of an existing category row.
    public void updateCategory(Category c) {
        String sql = "UPDATE Categories SET Name=?, Type=? WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, c.getName());
            pst.setString(2, c.getType());
            pst.setInt(3, c.getCategoryId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("updateCategory error: " + e.getMessage());
        }
    }

    // Deletes a category by its ID.
    public void deleteCategory(int categoryId) {
        String sql = "DELETE FROM Categories WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, categoryId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("deleteCategory error: " + e.getMessage());
        }
    }

    // Returns all categories stored in the database.
    public List<Category> loadCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("ID"),
                    rs.getString("Name"),
                    rs.getString("Type")
                ));
            }
        } catch (SQLException e) {
            System.out.println("loadCategories error: " + e.getMessage());
        }
        return list;
    }

    // Returns the category with the given ID, or null if not found.
    public Category loadCategory(int id) {
        String sql = "SELECT * FROM Categories WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Category(
                    rs.getInt("ID"),
                    rs.getString("Name"),
                    rs.getString("Type")
                );
            }
        } catch (SQLException e) {
            System.out.println("loadCategory error: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // Accounts
    // -------------------------------------------------------

    // Inserts a new account and returns the generated ID, or -1
    // on failure.
    public int saveAccount(Account a) {
        String sql =
            "INSERT INTO Accounts(Name, Type, OpeningBalance) VALUES(?,?,?)";
        try {
            PreparedStatement pst =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, a.getName());
            pst.setString(2, a.getType());
            pst.setDouble(3, a.getBalance());
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("saveAccount error: " + e.getMessage());
        }
        return -1;
    }

    // Updates the name, type, and current balance of an existing
    // account row.
    public void updateAccount(Account a) {
        String sql =
            "UPDATE Accounts SET Name=?, Type=?, OpeningBalance=? WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, a.getName());
            pst.setString(2, a.getType());
            pst.setDouble(3, a.getBalance());
            pst.setInt(4, a.getAccountId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("updateAccount error: " + e.getMessage());
        }
    }

    // Deletes an account and all of its transactions by account ID.
    public void deleteAccount(int accountId) {
        try {
            PreparedStatement del1 =
                connection.prepareStatement(
                    "DELETE FROM Transactions WHERE AccountID=?");
            del1.setInt(1, accountId);
            del1.executeUpdate();

            PreparedStatement del2 =
                connection.prepareStatement("DELETE FROM Accounts WHERE ID=?");
            del2.setInt(1, accountId);
            del2.executeUpdate();
        } catch (SQLException e) {
            System.out.println("deleteAccount error: " + e.getMessage());
        }
    }

    // Loads all accounts from the database, attaches each account's
    // transactions, and returns the populated list.
    public List<Account> loadAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Accounts";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Account a = new Account(
                    rs.getString("Name"),
                    rs.getString("Type"),
                    rs.getDouble("OpeningBalance")
                );
                a.setAccountId(rs.getInt("ID"));
                List<Transaction> txns =
                    loadTransactionsForAccount(a.getAccountId());
                for (Transaction t : txns) {
                    a.addTransaction(t);
                }
                list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("loadAccounts error: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Transactions
    // -------------------------------------------------------

    // Inserts a new transaction linked to the given account and
    // returns the generated ID, or -1 on failure. The category
    // must already be saved before calling this.
    public int saveTransaction(int accountId, Transaction t) {
        String sql =
            "INSERT INTO Transactions"
          + "(AccountID, CategoryID, Amount, Date, Description, Type)"
          + " VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement pst =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, accountId);
            pst.setInt(2, t.getCategory().getCategoryId());
            pst.setDouble(3, t.getAmount());
            pst.setString(4, t.getDate().toString());
            pst.setString(5, t.getDescription());
            pst.setString(6, (t instanceof Income) ? "Income" : "Expense");
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("saveTransaction error: " + e.getMessage());
        }
        return -1;
    }

    // Updates amount, date, description, and category for an
    // existing transaction row.
    public void updateTransaction(Transaction t) {
        String sql =
            "UPDATE Transactions"
          + " SET CategoryID=?, Amount=?, Date=?, Description=?"
          + " WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, t.getCategory().getCategoryId());
            pst.setDouble(2, t.getAmount());
            pst.setString(3, t.getDate().toString());
            pst.setString(4, t.getDescription());
            pst.setInt(5, t.getTransactionId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("updateTransaction error: " + e.getMessage());
        }
    }

    // Deletes a single transaction by its ID.
    public void deleteTransaction(int transactionId) {
        String sql = "DELETE FROM Transactions WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, transactionId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("deleteTransaction error: " + e.getMessage());
        }
    }

    // Loads all transactions for the given account, rebuilding each
    // as the correct Income or Expense subclass based on the stored
    // Type column.
    public List<Transaction> loadTransactionsForAccount(int accountId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE AccountID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, accountId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Category cat = loadCategory(rs.getInt("CategoryID"));
                if (cat == null) {
                    cat = new Category("Unknown", "Expense");
                }
                LocalDate date = LocalDate.parse(rs.getString("Date"));
                double amount   = rs.getDouble("Amount");
                String desc     = rs.getString("Description");
                String type     = rs.getString("Type");

                Transaction t;
                if ("Income".equalsIgnoreCase(type)) {
                    t = new Income(amount, date, desc, cat);
                } else {
                    t = new Expense(amount, date, desc, cat);
                }
                t.setTransactionId(rs.getInt("ID"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("loadTransactionsForAccount error: " + e.getMessage());
        }
        return list;
    }
}
