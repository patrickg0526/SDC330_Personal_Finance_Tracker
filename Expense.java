/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Concrete Transaction that represents money removed from an
 * account (e.g., groceries, rent, utilities). Extends Transaction
 * and provides the polymorphic applyTo implementation that
 * subtracts the amount from the current balance.
 */
import java.time.LocalDate;

public class Expense extends Transaction {
    public Expense(double amount, LocalDate date, String description,
            Category category) {
        super(amount, date, description, category);
    }

    @Override
    public double applyTo(double currentBalance) {
        return currentBalance - getAmount();
    }

    @Override
    protected String getType() {
        return "Expense";
    }
}
