/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Abstract super class for any money movement. Holds the shared
 * structure and behavior every transaction has and defines an
 * abstract applyTo method that each concrete subclass (Income or
 * Expense) implements to decide whether to add to or subtract from
 * an account balance. This is the piece that makes polymorphism
 * work - Account can iterate over a List<Transaction> and the right
 * applyTo is called at runtime without Account knowing or caring
 * about the concrete type.
 */
import java.time.LocalDate;

public abstract class Transaction {
    private int transactionId;
    private double amount;
    private LocalDate date;
    private String description;
    private Category category;

    public Transaction(double amount, LocalDate date, String description,
            Category category) {
        this.transactionId = 0;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    public int getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    // Each subclass decides whether this transaction adds to or
    // subtracts from the provided balance.
    public abstract double applyTo(double currentBalance);

    // Returns the transaction type label used in toString. Subclasses
    // override this to return "Income" or "Expense" so the super
    // class toString can remain shared.
    protected abstract String getType();

    @Override
    public String toString() {
        return String.format("%s | %-12s | %-7s | $%8.2f | %s",
            date.toString(),
            category.getName(),
            getType(),
            amount,
            description);
    }
}
