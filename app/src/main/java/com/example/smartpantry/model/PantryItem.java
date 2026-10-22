package com.example.smartpantry.model;

/**
 * Represents one pantry ingredient the user has at home.
 * Maps to one row in the "pantry" table.
 */
public class PantryItem {

    private long id;              // -1 = not yet in DB
    private String name;          // e.g. "tomato"
    private double quantity;      // e.g. 3.0
    private String unit;          // e.g. "pieces", "cups", "grams"
    private String expiryDate;    // ISO yyyy-MM-dd or null

    public PantryItem() {
        this.id = -1;
    }

    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this.id = -1;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // --- Getters and Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
    }
}
