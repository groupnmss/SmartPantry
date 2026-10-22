package com.example.smartpantry.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only data source for recipes and their required ingredients.
 * Recipes are seeded on database creation and are not modified by the user.
 */
public class RecipeDataSource {

    private static final String TAG = "RecipeDataSource";

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public RecipeDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) dbHelper.close();
    }

    /** Fetch all recipes with their ingredient lists attached. */
    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        Cursor recipeCursor = null;
        try {
            recipeCursor = database.query(
                    DatabaseHelper.TABLE_RECIPES,
                    null, null, null, null, null,
                    DatabaseHelper.RECIPE_NAME + " ASC"
            );
            while (recipeCursor.moveToNext()) {
                long recipeId = recipeCursor.getLong(
                        recipeCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_ID));
                String name = recipeCursor.getString(
                        recipeCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_NAME));
                String method = recipeCursor.getString(
                        recipeCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_METHOD));

                Recipe recipe = new Recipe(recipeId, name, method);
                recipe.setIngredients(getIngredientsForRecipe(recipeId));
                recipes.add(recipe);
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllRecipesWithIngredients failed", e);
        } finally {
            if (recipeCursor != null) recipeCursor.close();
        }
        return recipes;
    }

    /** Fetch a single recipe with its ingredient list, or null if not found. */
    public Recipe getRecipeById(long recipeId) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_RECIPES,
                    null,
                    DatabaseHelper.RECIPE_ID + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    null, null, null
            );
            if (cursor.moveToFirst()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_NAME));
                String method = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_METHOD));
                Recipe recipe = new Recipe(recipeId, name, method);
                recipe.setIngredients(getIngredientsForRecipe(recipeId));
                return recipe;
            }
        } catch (Exception e) {
            Log.e(TAG, "getRecipeById failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                    null,
                    DatabaseHelper.RI_RECIPE_ID + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    null, null,
                    DatabaseHelper.RI_NAME + " ASC"
            );
            while (cursor.moveToNext()) {
                RecipeIngredient ing = new RecipeIngredient();
                ing.setId(cursor.getLong(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.RI_ID)));
                ing.setRecipeId(recipeId);
                ing.setName(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.RI_NAME)));
                ing.setQuantity(cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.RI_QUANTITY)));
                ing.setUnit(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.RI_UNIT)));
                list.add(ing);
            }
        } catch (Exception e) {
            Log.e(TAG, "getIngredientsForRecipe failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }
}
