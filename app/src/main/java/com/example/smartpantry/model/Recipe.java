package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one recipe in the app.
 * A Recipe has a name, preparation method, and a list of required ingredients.
 */
public class Recipe {

    private long id;
    private String name;
    private String method;                     // preparation steps
    private List<RecipeIngredient> ingredients; // what the recipe needs

    public Recipe() {
        this.id = -1;
        this.ingredients = new ArrayList<>();
    }

    public Recipe(long id, String name, String method) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.ingredients = new ArrayList<>();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
    public void addIngredient(RecipeIngredient ing) { this.ingredients.add(ing); }

    @Override
    public String toString() {
        return name + " (" + ingredients.size() + " ingredients)";
    }
}
