package com.example.smartpantry.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates and manages the SQLite database for Smart Pantry Manager.
 *
 * Tables:
 *   - pantry:             ingredients the user has at home
 *   - recipes:            recipes available in the app
 *   - recipe_ingredients: many-to-one — each row is one required ingredient of one recipe
 *
 * On first run, the database is populated with 20 seed recipes and their
 * ingredient lists, so the "Suggested Recipes" screen has data from day one.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    // ---------- PANTRY TABLE ----------
    public static final String TABLE_PANTRY = "pantry";
    public static final String PANTRY_ID = "_id";
    public static final String PANTRY_NAME = "name";
    public static final String PANTRY_QUANTITY = "quantity";
    public static final String PANTRY_UNIT = "unit";
    public static final String PANTRY_EXPIRY = "expiry_date";

    // ---------- RECIPES TABLE ----------
    public static final String TABLE_RECIPES = "recipes";
    public static final String RECIPE_ID = "_id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_METHOD = "method";

    // ---------- RECIPE_INGREDIENTS TABLE ----------
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String RI_ID = "_id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_NAME = "name";
    public static final String RI_QUANTITY = "quantity";
    public static final String RI_UNIT = "unit";

    // ---------- CREATE STATEMENTS ----------
    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PANTRY_NAME + " TEXT NOT NULL, " +
                    PANTRY_QUANTITY + " REAL NOT NULL, " +
                    PANTRY_UNIT + " TEXT NOT NULL, " +
                    PANTRY_EXPIRY + " TEXT" +
                    ");";

    private static final String CREATE_TABLE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RECIPE_NAME + " TEXT NOT NULL, " +
                    RECIPE_METHOD + " TEXT NOT NULL" +
                    ");";

    private static final String CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RI_RECIPE_ID + " INTEGER NOT NULL, " +
                    RI_NAME + " TEXT NOT NULL, " +
                    RI_QUANTITY + " REAL NOT NULL, " +
                    RI_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + RI_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + RECIPE_ID + ") ON DELETE CASCADE" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);
        seedRecipes(db);
        Log.i(TAG, "Database created and 20 recipes seeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ==================== SEED DATA ====================

    private void seedRecipes(SQLiteDatabase db) {

        // --- 1. Scrambled Eggs ---
        long id = insertRecipe(db, "Scrambled Eggs",
                "1. Crack the eggs into a bowl and whisk.\n" +
                "2. Melt butter in a pan over medium heat.\n" +
                "3. Pour in eggs and stir gently for 2-3 minutes.\n" +
                "4. Season with salt and pepper. Serve immediately.");
        insertIngredient(db, id, "egg", 3, "pieces");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "salt", 1, "tsp");
        insertIngredient(db, id, "pepper", 1, "tsp");

        // --- 2. Spaghetti Aglio e Olio ---
        id = insertRecipe(db, "Spaghetti Aglio e Olio",
                "1. Cook spaghetti in salted boiling water until al dente.\n" +
                "2. Meanwhile, heat olive oil and sliced garlic in a pan.\n" +
                "3. Add chilli flakes and drained spaghetti.\n" +
                "4. Toss, garnish with parsley, and serve.");
        insertIngredient(db, id, "spaghetti", 200, "grams");
        insertIngredient(db, id, "garlic", 3, "clove");
        insertIngredient(db, id, "olive oil", 3, "tbsp");
        insertIngredient(db, id, "chilli flakes", 1, "tsp");
        insertIngredient(db, id, "parsley", 1, "tbsp");

        // --- 3. Grilled Cheese Sandwich ---
        id = insertRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter one side of each bread slice.\n" +
                "2. Place cheese between the unbuttered sides.\n" +
                "3. Grill in a pan over medium heat until golden on both sides.\n" +
                "4. Cut in half and serve warm.");
        insertIngredient(db, id, "bread", 2, "pieces");
        insertIngredient(db, id, "cheese", 50, "grams");
        insertIngredient(db, id, "butter", 1, "tbsp");

        // --- 4. Tomato Soup ---
        id = insertRecipe(db, "Tomato Soup",
                "1. Sauté chopped onion and garlic in olive oil.\n" +
                "2. Add chopped tomatoes and stock, simmer 20 minutes.\n" +
                "3. Blend until smooth.\n" +
                "4. Stir in cream, season, and serve.");
        insertIngredient(db, id, "tomato", 5, "pieces");
        insertIngredient(db, id, "onion", 1, "pieces");
        insertIngredient(db, id, "garlic", 2, "clove");
        insertIngredient(db, id, "stock", 500, "ml");
        insertIngredient(db, id, "cream", 100, "ml");

        // --- 5. Omelette ---
        id = insertRecipe(db, "Omelette",
                "1. Beat eggs with milk, salt and pepper.\n" +
                "2. Pour into hot buttered pan.\n" +
                "3. Add grated cheese and chopped onion.\n" +
                "4. Fold and serve.");
        insertIngredient(db, id, "egg", 2, "pieces");
        insertIngredient(db, id, "milk", 50, "ml");
        insertIngredient(db, id, "cheese", 30, "grams");
        insertIngredient(db, id, "onion", 1, "pieces");

        // --- 6. Pancakes ---
        id = insertRecipe(db, "Pancakes",
                "1. Mix flour, sugar, and baking powder.\n" +
                "2. Whisk in milk and egg until smooth.\n" +
                "3. Cook spoonfuls in a hot pan for 2 min each side.\n" +
                "4. Serve with syrup.");
        insertIngredient(db, id, "flour", 200, "grams");
        insertIngredient(db, id, "milk", 250, "ml");
        insertIngredient(db, id, "egg", 1, "pieces");
        insertIngredient(db, id, "sugar", 2, "tbsp");
        insertIngredient(db, id, "baking powder", 1, "tsp");

        // --- 7. Pasta with Tomato Sauce ---
        id = insertRecipe(db, "Pasta with Tomato Sauce",
                "1. Cook pasta in salted water.\n" +
                "2. Sauté garlic in olive oil, add chopped tomatoes.\n" +
                "3. Simmer 10 minutes and add basil.\n" +
                "4. Toss with pasta and serve.");
        insertIngredient(db, id, "pasta", 200, "grams");
        insertIngredient(db, id, "tomato", 4, "pieces");
        insertIngredient(db, id, "garlic", 2, "clove");
        insertIngredient(db, id, "olive oil", 2, "tbsp");
        insertIngredient(db, id, "basil", 1, "tbsp");

        // --- 8. French Toast ---
        id = insertRecipe(db, "French Toast",
                "1. Beat egg, milk, cinnamon, and sugar in a shallow dish.\n" +
                "2. Dip bread slices in the mixture.\n" +
                "3. Fry in butter until golden on both sides.\n" +
                "4. Serve with syrup or honey.");
        insertIngredient(db, id, "bread", 4, "pieces");
        insertIngredient(db, id, "egg", 2, "pieces");
        insertIngredient(db, id, "milk", 100, "ml");
        insertIngredient(db, id, "cinnamon", 1, "tsp");
        insertIngredient(db, id, "sugar", 1, "tbsp");

        // --- 9. Fried Rice ---
        id = insertRecipe(db, "Fried Rice",
                "1. Cook rice and let it cool.\n" +
                "2. Heat oil, scramble egg, add onion.\n" +
                "3. Add rice and soy sauce, stir fry 3 min.\n" +
                "4. Serve hot.");
        insertIngredient(db, id, "rice", 300, "grams");
        insertIngredient(db, id, "egg", 2, "pieces");
        insertIngredient(db, id, "soy sauce", 2, "tbsp");
        insertIngredient(db, id, "onion", 1, "pieces");
        insertIngredient(db, id, "oil", 2, "tbsp");

        // --- 10. Chicken Stir Fry ---
        id = insertRecipe(db, "Chicken Stir Fry",
                "1. Cut chicken into strips.\n" +
                "2. Heat oil and stir fry chicken until cooked.\n" +
                "3. Add vegetables and soy sauce, cook 5 minutes.\n" +
                "4. Serve over rice.");
        insertIngredient(db, id, "chicken", 300, "grams");
        insertIngredient(db, id, "rice", 250, "grams");
        insertIngredient(db, id, "soy sauce", 2, "tbsp");
        insertIngredient(db, id, "vegetables", 200, "grams");
        insertIngredient(db, id, "garlic", 2, "clove");

        // --- 11. Oatmeal ---
        id = insertRecipe(db, "Oatmeal",
                "1. Bring milk to a simmer in a pot.\n" +
                "2. Stir in oats and cook 5 minutes.\n" +
                "3. Sweeten with sugar and cinnamon.\n" +
                "4. Serve warm.");
        insertIngredient(db, id, "oats", 100, "grams");
        insertIngredient(db, id, "milk", 250, "ml");
        insertIngredient(db, id, "sugar", 1, "tbsp");
        insertIngredient(db, id, "cinnamon", 1, "tsp");

        // --- 12. Quesadilla ---
        id = insertRecipe(db, "Quesadilla",
                "1. Lay tortilla flat.\n" +
                "2. Sprinkle cheese and cooked chicken on half.\n" +
                "3. Fold and fry in a dry pan until golden.\n" +
                "4. Slice into wedges.");
        insertIngredient(db, id, "tortilla", 1, "pieces");
        insertIngredient(db, id, "cheese", 50, "grams");
        insertIngredient(db, id, "chicken", 100, "grams");
        insertIngredient(db, id, "peppers", 1, "pieces");

        // --- 13. Macaroni & Cheese ---
        id = insertRecipe(db, "Macaroni & Cheese",
                "1. Cook macaroni and drain.\n" +
                "2. Make a roux with butter and flour, add milk.\n" +
                "3. Stir in cheese until melted.\n" +
                "4. Combine with pasta and serve.");
        insertIngredient(db, id, "macaroni", 250, "grams");
        insertIngredient(db, id, "cheese", 150, "grams");
        insertIngredient(db, id, "milk", 300, "ml");
        insertIngredient(db, id, "butter", 2, "tbsp");
        insertIngredient(db, id, "flour", 2, "tbsp");

        // --- 14. Garden Salad ---
        id = insertRecipe(db, "Garden Salad",
                "1. Chop lettuce, cucumber, and tomato.\n" +
                "2. Whisk olive oil and vinegar.\n" +
                "3. Toss everything together.\n" +
                "4. Season and serve cold.");
        insertIngredient(db, id, "lettuce", 1, "pieces");
        insertIngredient(db, id, "cucumber", 1, "pieces");
        insertIngredient(db, id, "tomato", 2, "pieces");
        insertIngredient(db, id, "olive oil", 2, "tbsp");
        insertIngredient(db, id, "vinegar", 1, "tbsp");

        // --- 15. Mashed Potatoes ---
        id = insertRecipe(db, "Mashed Potatoes",
                "1. Peel and boil potatoes until soft.\n" +
                "2. Drain and mash.\n" +
                "3. Stir in butter and milk.\n" +
                "4. Season with salt.");
        insertIngredient(db, id, "potato", 500, "grams");
        insertIngredient(db, id, "butter", 2, "tbsp");
        insertIngredient(db, id, "milk", 100, "ml");
        insertIngredient(db, id, "salt", 1, "tsp");

        // --- 16. Banana Smoothie ---
        id = insertRecipe(db, "Banana Smoothie",
                "1. Peel banana and place in blender.\n" +
                "2. Add milk, yogurt, and honey.\n" +
                "3. Blend until smooth.\n" +
                "4. Serve chilled.");
        insertIngredient(db, id, "banana", 1, "pieces");
        insertIngredient(db, id, "milk", 200, "ml");
        insertIngredient(db, id, "yogurt", 100, "grams");
        insertIngredient(db, id, "honey", 1, "tbsp");

        // --- 17. Beef Tacos ---
        id = insertRecipe(db, "Beef Tacos",
                "1. Brown mince with chopped onion.\n" +
                "2. Add spices and a little water.\n" +
                "3. Warm tortillas.\n" +
                "4. Fill tortillas with beef and tomato, serve.");
        insertIngredient(db, id, "mince", 300, "grams");
        insertIngredient(db, id, "tortilla", 4, "pieces");
        insertIngredient(db, id, "onion", 1, "pieces");
        insertIngredient(db, id, "tomato", 2, "pieces");
        insertIngredient(db, id, "spices", 1, "tbsp");

        // --- 18. Vegetable Curry ---
        id = insertRecipe(db, "Vegetable Curry",
                "1. Sauté chopped onion in oil.\n" +
                "2. Add curry powder, chopped potatoes and carrots.\n" +
                "3. Pour in coconut milk, simmer 25 min.\n" +
                "4. Serve with rice.");
        insertIngredient(db, id, "potato", 300, "grams");
        insertIngredient(db, id, "carrot", 2, "pieces");
        insertIngredient(db, id, "onion", 1, "pieces");
        insertIngredient(db, id, "curry powder", 2, "tbsp");
        insertIngredient(db, id, "coconut milk", 400, "ml");

        // --- 19. Bread Pudding ---
        id = insertRecipe(db, "Bread Pudding",
                "1. Tear bread into pieces.\n" +
                "2. Whisk milk, egg, sugar, and cinnamon.\n" +
                "3. Pour over bread and soak 10 minutes.\n" +
                "4. Bake 30 min at 180°C.");
        insertIngredient(db, id, "bread", 4, "pieces");
        insertIngredient(db, id, "milk", 300, "ml");
        insertIngredient(db, id, "egg", 2, "pieces");
        insertIngredient(db, id, "sugar", 3, "tbsp");
        insertIngredient(db, id, "cinnamon", 1, "tsp");

        // --- 20. Garlic Butter Pasta ---
        id = insertRecipe(db, "Garlic Butter Pasta",
                "1. Cook pasta in salted water.\n" +
                "2. Melt butter, add minced garlic and cook 1 min.\n" +
                "3. Toss drained pasta with garlic butter.\n" +
                "4. Sprinkle parmesan and serve.");
        insertIngredient(db, id, "pasta", 200, "grams");
        insertIngredient(db, id, "garlic", 3, "clove");
        insertIngredient(db, id, "butter", 3, "tbsp");
        insertIngredient(db, id, "parmesan", 50, "grams");
    }

    private long insertRecipe(SQLiteDatabase db, String name, String method) {
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put(RECIPE_NAME, name);
        cv.put(RECIPE_METHOD, method);
        return db.insert(TABLE_RECIPES, null, cv);
    }

    private void insertIngredient(SQLiteDatabase db, long recipeId,
                                  String name, double quantity, String unit) {
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put(RI_RECIPE_ID, recipeId);
        cv.put(RI_NAME, name);
        cv.put(RI_QUANTITY, quantity);
        cv.put(RI_UNIT, unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, cv);
    }
}
