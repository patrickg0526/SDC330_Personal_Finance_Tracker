/******************************************************************
 * Name: Patrick Gonzalez
 * Date: 05/07/2026
 * Assignment: SDC330 Course Project - Phase 3 Final Submission
 *
 * Main application / controller class for the Personal Finance
 * Tracker. Owns the in-memory collections of accounts and
 * categories and drives the menu-based user interface. All output
 * formatting is delegated to the domain classes' toString methods
 * so this class does not have to know how any given object prints
 * itself. Phase 2 wired in full database persistence. Phase 3
 * exposes the full CRUD surface in the UI: accounts and
 * transactions can now be deleted, and transaction details can be
 * updated.
 */
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class FinanceTracker {

    private ArrayList<Account> accounts;
    private ArrayList<Category> categories;
    private DatabaseManager db;
    private Scanner in;

    public FinanceTracker() {
        this.accounts = new ArrayList<Account>();
        this.categories = new ArrayList<Category>();
        this.db = new DatabaseManager();
        this.in = new Scanner(System.in);
    }

    public static void main(String[] args) {
        System.out.println("Patrick Gonzalez - SDC330 Course Project (Phase 3)");
        System.out.println("Personal Finance Tracker\n");
        FinanceTracker app = new FinanceTracker();
        app.loadFromDatabase();
        if (app.accounts.isEmpty()) {
            app.seedSampleData();
            app.saveAllToDatabase();
        }
        app.run();
        app.db.disconnect();
    }

    private void seedSampleData() {
        Category paycheck  = new Category("Paycheck",   "Income");
        Category groceries = new Category("Groceries",  "Expense");
        Category rent      = new Category("Rent",       "Expense");
        Category utilities = new Category("Utilities",  "Expense");
        categories.add(paycheck);
        categories.add(groceries);
        categories.add(rent);
        categories.add(utilities);
        Account checking = new Account("Main Checking", "Checking", 0.00);
        checking.addTransaction(new Income( 2500.00, LocalDate.of(2026, 4,  1), "Bi-weekly paycheck",      paycheck));
        checking.addTransaction(new Expense(1200.00, LocalDate.of(2026, 4,  3), "April rent",              rent));
        checking.addTransaction(new Expense( 135.42, LocalDate.of(2026, 4,  5), "Weekly grocery run",      groceries));
        checking.addTransaction(new Expense(  84.19, LocalDate.of(2026, 4,  8), "Electric bill",           utilities));
        accounts.add(checking);
        Account savings = new Account("Emergency Savings", "Savings", 3000.00);
        savings.addTransaction(new Income(250.00, LocalDate.of(2026, 4, 1), "Auto-transfer from checking", paycheck));
        accounts.add(savings);
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            printMenu();
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1": displayAccounts();             break;
                case "2": displayTransactionHistory();   break;
                case "3": displaySpendingSummary();      break;
                case "4": addAccount();                  break;
                case "5": addTransaction();              break;
                case "6": updateTransaction();           break;
                case "7": deleteTransaction();           break;
                case "8": deleteAccount();               break;
                case "0": keepGoing = false;             break;
                default:  System.out.println("Not a valid choice. Try again.\n");
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println("-----------------------------------");
        System.out.println("1. View all accounts");
        System.out.println("2. View transaction history for an account");
        System.out.println("3. View spending summary for an account");
        System.out.println("4. Add a new account");
        System.out.println("5. Add a transaction to an account");
        System.out.println("6. Update a transaction");
        System.out.println("7. Delete a transaction");
        System.out.println("8. Delete an account");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }

    public void displayAccounts() {
        System.out.println("\nAccounts");
        for (Account a : accounts) { System.out.println("  " + a); }
        System.out.println();
    }

    public void displayTransactionHistory() {
        Account a = pickAccount();
        if (a == null) return;
        System.out.println("\nTransaction history for " + a.getName());
        for (Transaction t : a.getTransactionHistory()) {
            System.out.println("  [" + t.getTransactionId() + "] " + t);
        }
        System.out.println();
    }

    public void displaySpendingSummary() {
        Account a = pickAccount();
        if (a == null) return;
        System.out.println("\nSpending summary for " + a.getName());
        Map<Category, Double> totals = a.getSpendingSummaryByCategory();
        for (Map.Entry<Category, Double> e : totals.entrySet()) {
            System.out.printf("  %-15s $%.2f%n", e.getKey().getName(), e.getValue());
        }
        System.out.println();
    }

    public void addAccount() {
        System.out.print("Account name: ");
        String name = in.nextLine().trim();
        System.out.print("Account type (Checking/Savings): ");
        String type = in.nextLine().trim();
        System.out.print("Opening balance: ");
        double opening = Double.parseDouble(in.nextLine().trim());
        Account a = new Account(name, type, opening);
        int id = db.saveAccount(a);
        a.setAccountId(id);
        accounts.add(a);
        System.out.println("Added: " + a + "\n");
    }

    public void addTransaction() {
        Account a = pickAccount();
        if (a == null) return;
        System.out.print("Type (Income/Expense): ");
        String kind = in.nextLine().trim();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(in.nextLine().trim());
        System.out.print("Description: ");
        String desc = in.nextLine().trim();
        System.out.print("Category name: ");
        String catName = in.nextLine().trim();
        Category cat = findOrCreateCategory(catName, kind);
        Transaction t;
        if (kind.equalsIgnoreCase("Income")) {
            t = new Income(amount, LocalDate.now(), desc, cat);
        } else {
            t = new Expense(amount, LocalDate.now(), desc, cat);
        }
        a.addTransaction(t);
        if (cat.getCategoryId() == 0) {
            int catId = db.saveCategory(cat);
            cat.setCategoryId(catId);
        }
        int txId = db.saveTransaction(a.getAccountId(), t);
        t.setTransactionId(txId);
        db.updateAccount(a);
        System.out.println("Added. New balance: $" + String.format("%.2f", a.getBalance()) + "\n");
    }

    // Allows the user to change the amount and description of an
    // existing transaction and persists both changes to the database.
    public void updateTransaction() {
        Account a = pickAccount();
        if (a == null) return;
        ArrayList<Transaction> txns = a.getTransactionHistory();
        if (txns.isEmpty()) { System.out.println("No transactions for this account.\n"); return; }
        printNumberedTransactions(txns);
        System.out.print("Pick transaction to update (1-" + txns.size() + "): ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            Transaction t = txns.get(idx);
            System.out.print("New amount (current: $" + String.format("%.2f", t.getAmount()) + "): ");
            double newAmount = Double.parseDouble(in.nextLine().trim());
            System.out.print("New description (current: " + t.getDescription() + "): ");
            String newDesc = in.nextLine().trim();
            if (t instanceof Income) {
                a.setBalance(a.getBalance() - t.getAmount() + newAmount);
            } else {
                a.setBalance(a.getBalance() + t.getAmount() - newAmount);
            }
            t.setAmount(newAmount);
            t.setDescription(newDesc);
            db.updateTransaction(t);
            db.updateAccount(a);
            System.out.println("Updated. New balance: $" + String.format("%.2f", a.getBalance()) + "\n");
        } catch (Exception e) { System.out.println("Not a valid pick.\n"); }
    }

    // Removes a selected transaction from the account in memory and
    // in the database, then syncs the account balance.
    public void deleteTransaction() {
        Account a = pickAccount();
        if (a == null) return;
        ArrayList<Transaction> txns = a.getTransactionHistory();
        if (txns.isEmpty()) { System.out.println("No transactions for this account.\n"); return; }
        printNumberedTransactions(txns);
        System.out.print("Pick transaction to delete (1-" + txns.size() + "): ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            Transaction t = txns.get(idx);
            int txId = t.getTransactionId();
            a.removeTransaction(txId);
            db.deleteTransaction(txId);
            db.updateAccount(a);
            System.out.println("Transaction deleted. New balance: $" + String.format("%.2f", a.getBalance()) + "\n");
        } catch (Exception e) { System.out.println("Not a valid pick.\n"); }
    }

    // Removes an account and all its transactions after a yes/no
    // confirmation, from both memory and the database.
    public void deleteAccount() {
        Account a = pickAccount();
        if (a == null) return;
        System.out.print("Delete " + a.getName() + " and all its transactions? (yes/no): ");
        String confirm = in.nextLine().trim();
        if (confirm.equalsIgnoreCase("yes")) {
            db.deleteAccount(a.getAccountId());
            accounts.remove(a);
            System.out.println("Account deleted.\n");
        } else {
            System.out.println("Cancelled.\n");
        }
    }

    private void printNumberedTransactions(ArrayList<Transaction> txns) {
        System.out.println("Transactions:");
        for (int i = 0; i < txns.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + txns.get(i));
        }
    }

    private Account pickAccount() {
        if (accounts.isEmpty()) { System.out.println("No accounts yet.\n"); return null; }
        System.out.println("Accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + accounts.get(i).getName());
        }
        System.out.print("Pick one (1-" + accounts.size() + "): ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            return accounts.get(idx);
        } catch (Exception e) { System.out.println("Not a valid pick.\n"); return null; }
    }

    private Category findOrCreateCategory(String name, String type) {
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        Category c = new Category(name, type);
        categories.add(c);
        return c;
    }

    public void loadFromDatabase() {
        db.connect();
        accounts   = new ArrayList<Account>(db.loadAccounts());
        categories = new ArrayList<Category>(db.loadCategories());
    }

    public void saveAllToDatabase() {
        for (Category c : categories) {
            int id = db.saveCategory(c);
            c.setCategoryId(id);
        }
        for (Account a : accounts) {
            int accountId = db.saveAccount(a);
            a.setAccountId(accountId);
            for (Transaction t : a.getTransactionHistory()) {
                int txId = db.saveTransaction(accountId, t);
                t.setTransactionId(txId);
            }
        }
    }
}
