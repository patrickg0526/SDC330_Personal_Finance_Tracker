/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Main application / controller class for the Personal Finance
 * Tracker. Owns the in-memory collections of accounts and
 * categories and drives the menu-based user interface. All output
 * formatting is delegated to the domain classes' toString methods
 * so this class does not have to know how any given object prints
 * itself. Database interaction is called through DatabaseManager,
 * which is a stub for Phase 1 and will be wired up in the Phase 2
 * (Database Implementation) submission.
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
        System.out.println("Patrick Gonzalez - SDC330 Course Project (Phase 1)");
        System.out.println("Personal Finance Tracker\n");

        FinanceTracker app = new FinanceTracker();
        app.seedSampleData();
        app.run();
    }

    // Loads a small set of sample accounts, categories, and
    // transactions so the Phase 1 demo has something to show
    // without the database being wired up yet.
    private void seedSampleData() {
        Category paycheck = new Category("Paycheck", "Income");
        Category groceries = new Category("Groceries", "Expense");
        Category rent = new Category("Rent", "Expense");
        Category utilities = new Category("Utilities", "Expense");
        categories.add(paycheck);
        categories.add(groceries);
        categories.add(rent);
        categories.add(utilities);

        Account checking = new Account("Main Checking", "Checking", 0.00);
        checking.addTransaction(new Income(2500.00,
            LocalDate.of(2026, 4, 1), "Bi-weekly paycheck", paycheck));
        checking.addTransaction(new Expense(1200.00,
            LocalDate.of(2026, 4, 3), "April rent", rent));
        checking.addTransaction(new Expense(135.42,
            LocalDate.of(2026, 4, 5), "Weekly grocery run", groceries));
        checking.addTransaction(new Expense(84.19,
            LocalDate.of(2026, 4, 8), "Electric bill", utilities));
        accounts.add(checking);

        Account savings = new Account("Emergency Savings", "Savings", 3000.00);
        savings.addTransaction(new Income(250.00,
            LocalDate.of(2026, 4, 1), "Auto-transfer from checking", paycheck));
        accounts.add(savings);
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            printMenu();
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1":
                    displayAccounts();
                    break;
                case "2":
                    displayTransactionHistory();
                    break;
                case "3":
                    displaySpendingSummary();
                    break;
                case "4":
                    addAccount();
                    break;
                case "5":
                    addTransaction();
                    break;
                case "0":
                    keepGoing = false;
                    break;
                default:
                    System.out.println("Not a valid choice. Try again.\n");
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
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }

    public void displayAccounts() {
        System.out.println("\nAccounts");
        for (Account a : accounts) {
            System.out.println("  " + a);
        }
        System.out.println();
    }

    public void displayTransactionHistory() {
        Account a = pickAccount();
        if (a == null) return;
        System.out.println("\nTransaction history for " + a.getName());
        for (Transaction t : a.getTransactionHistory()) {
            System.out.println("  " + t);
        }
        System.out.println();
    }

    public void displaySpendingSummary() {
        Account a = pickAccount();
        if (a == null) return;
        System.out.println("\nSpending summary for " + a.getName());
        Map<Category, Double> totals = a.getSpendingSummaryByCategory();
        for (Map.Entry<Category, Double> e : totals.entrySet()) {
            System.out.printf("  %-15s $%.2f%n",
                e.getKey().getName(), e.getValue());
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
        System.out.println("Added. New balance: $" + a.getBalance() + "\n");
    }

    private Account pickAccount() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet.\n");
            return null;
        }
        System.out.println("Accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + accounts.get(i).getName());
        }
        System.out.print("Pick one (1-" + accounts.size() + "): ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            return accounts.get(idx);
        } catch (Exception e) {
            System.out.println("Not a valid pick.\n");
            return null;
        }
    }

    private Category findOrCreateCategory(String name, String type) {
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        Category c = new Category(name, type);
        categories.add(c);
        return c;
    }

    public void loadFromDatabase() {
        // Phase 2 will populate accounts, categories, and
        // transactions from the DatabaseManager on startup.
        db.connect();
        accounts = new ArrayList<Account>(db.loadAccounts());
    }
}
