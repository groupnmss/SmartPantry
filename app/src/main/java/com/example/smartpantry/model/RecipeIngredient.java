package com.example.smartpantry.model;

/**
 * One ingredient required by a recipe.
 * Example: Recipe "Omelette" needs "egg" x 3 "pieces".
 */
public class RecipeIngredient {

    private long id;
    private long recipeId;      // foreign key to Recipe
    private String name;        // e.g. "egg"
    private double quantity;    // e.g. 3.0
    private String unit;        // e.g. "pieces", "cups"

    public RecipeIngredient() { this.id = -1; }

    public RecipeIngredient(long recipeId, String name, double quantity, String unit) {
        this.id = -1;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RecipeIngredient(long id, long recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getRecipeId() { return recipeId; }
    public void setRecipeId(long recipeId) { this.recipeId = recipeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        return quantity + " " + unit + " " + name;
    }
}
