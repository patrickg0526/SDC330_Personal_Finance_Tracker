/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Concrete Transaction that represents money added to an account
 * (e.g., a paycheck or a refund). Extends Transaction and provides
 * the polymorphic applyTo implementation that adds the amount to
 * the current balance.
 */
import java.time.LocalDate;

public class Income extends Transaction {
    public Income(double amount, LocalDate date, String description,
            Category category) {
        super(amount, date, description, category);
    }

    @Override
    public double applyTo(double currentBalance) {
        return currentBalance + getAmount();
    }

    @Override
    protected String getType() {
        return "Income";
    }
}
