/******************************************************************
 * Name: Patrick Gonzalez
 * Date: April 24, 2026
 * Assignment: SDC330 Course Project - Class Implementation (Phase 1)
 *
 * Represents a user-defined category (e.g., Groceries, Rent,
 * Paycheck). Each category is either an Income or an Expense
 * category, which keeps summary reports consistent. The class is
 * used via composition by Transaction. Private fields with public
 * accessors demonstrate encapsulation; toString returns the display
 * name so the category renders cleanly in transaction listings.
 */
public class Category {
    private int categoryId;
    private String name;
    private String type;

    // Constructor used when creating a brand-new category that has
    // not been saved to the database yet. categoryId is left as 0
    // until the database assigns one.
    public Category(String name, String type) {
        this.categoryId = 0;
        this.name = name;
        this.type = type;
    }

    // Constructor used when reading an existing category back from
    // the database, where the id is already known.
    public Category(int categoryId, String name, String type) {
        this.categoryId = categoryId;
        this.name = name;
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    @Override
    public String toString() {
        return name;
    }
}
