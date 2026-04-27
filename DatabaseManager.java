/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Service class that will encapsulate all JDBC/SQL interaction for
 * the Personal Finance Tracker. Phase 1 only requires the class
 * skeleton - the real implementations are scheduled for Phase 2
 * (Database Implementation) in Week 4. Each stub method is defined
 * here so the rest of the application can already call into it and
 * because it documents the CRUD surface the database layer will
 * provide.
 */
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private String connectionUrl;
    private Object connection;

    public DatabaseManager() {
        this.connectionUrl = "jdbc:sqlite:finance.db";
        this.connection = null;
    }

    public void connect() {
        // Phase 2: open a JDBC connection using connectionUrl.
    }

    public void disconnect() {
        // Phase 2: close the JDBC connection.
    }

    // Accounts CRUD
    public int saveAccount(Account a) {
        // Phase 2: INSERT into Accounts, return generated id.
        return 0;
    }

    public void updateAccount(Account a) {
        // Phase 2: UPDATE the matching row.
    }

    public void deleteAccount(int accountId) {
        // Phase 2: DELETE the matching row.
    }

    public List<Account> loadAccounts() {
        // Phase 2: SELECT all accounts, build Account objects,
        // attach their transactions, return the list.
        return new ArrayList<Account>();
    }

    // Transactions CRUD
    public int saveTransaction(int accountId, Transaction t) {
        // Phase 2: INSERT into Transactions, return generated id.
        return 0;
    }

    public void updateTransaction(Transaction t) {
        // Phase 2: UPDATE the matching row.
    }

    public void deleteTransaction(int transactionId) {
        // Phase 2: DELETE the matching row.
    }

    public List<Transaction> loadTransactionsForAccount(int accountId) {
        // Phase 2: SELECT transactions for the given account and
        // build Income or Expense objects based on the stored type.
        return new ArrayList<Transaction>();
    }

    // Categories CRUD
    public int saveCategory(Category c) {
        // Phase 2: INSERT into Categories, return generated id.
        return 0;
    }

    public void updateCategory(Category c) {
        // Phase 2: UPDATE the matching row.
    }

    public void deleteCategory(int categoryId) {
        // Phase 2: DELETE the matching row.
    }

    public List<Category> loadCategories() {
        // Phase 2: SELECT all categories.
        return new ArrayList<Category>();
    }
}
