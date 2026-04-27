/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Represents a single bank account. Demonstrates composition - an
 * Account owns its list of Transaction objects, the list is created
 * with the account and goes away with it. The balance is only
 * updated by addTransaction and removeTransaction so the running
 * balance and the list can never drift apart. addTransaction
 * accepts Transaction so Income and Expense work interchangeably;
 * the correct applyTo is selected by polymorphism at runtime.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Account {
    private int accountId;
    private String name;
    private String type;
    private double balance;
    private ArrayList<Transaction> transactions;

    public Account(String name, String type, double openingBalance) {
        this.accountId = 0;
        this.name = name;
        this.type = type;
        this.balance = openingBalance;
        this.transactions = new ArrayList<Transaction>();
    }

    public int getAccountId() {
        return accountId;
    }
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Adds a transaction and updates the balance using the
    // transaction's polymorphic applyTo method so the right math
    // is applied whether it is an Income or an Expense.
    public void addTransaction(Transaction t) {
        transactions.add(t);
        balance = t.applyTo(balance);
    }

    // Removes the transaction with the matching id and reverses
    // its effect on the balance. Returns true if a transaction
    // was found and removed.
    public boolean removeTransaction(int transactionId) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getTransactionId() == transactionId) {
                // Reverse the effect: re-applying an opposite-signed
                // amount by treating the stored amount as negative.
                // Income was +amount, so subtract it; Expense was
                // -amount, so add it. Easiest way is to undo using
                // a sign flip driven by the concrete type.
                if (t instanceof Income) {
                    balance -= t.getAmount();
                } else if (t instanceof Expense) {
                    balance += t.getAmount();
                }
                transactions.remove(i);
                return true;
            }
        }
        return false;
    }

    // Returns a shallow copy of the transaction list so callers
    // cannot mutate the internal list directly.
    public ArrayList<Transaction> getTransactionHistory() {
        return new ArrayList<Transaction>(transactions);
    }

    // Totals the transactions by Category for use in the spending
    // summary report.
    public Map<Category, Double> getSpendingSummaryByCategory() {
        Map<Category, Double> totals = new HashMap<Category, Double>();
        for (Transaction t : transactions) {
            Category c = t.getCategory();
            double running = totals.getOrDefault(c, 0.0);
            totals.put(c, running + t.getAmount());
        }
        return totals;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Balance: $%.2f",
            name, type, balance);
    }
}
