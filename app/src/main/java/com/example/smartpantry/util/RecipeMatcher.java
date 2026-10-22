package com.example.smartpantry.util;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core strict-matching algorithm for Smart Pantry Manager.
 *
 * A recipe is suggested ONLY if EVERY required ingredient is present in the
 * user's pantry in AT LEAST the required quantity, after normalizing for:
 *   - Case (Tomato == tomato)
 *   - Whitespace
 *   - Plural forms (tomatoes -> tomato, berries -> berry)
 *   - Unit differences (cups/tbsp/tsp -> ml, kg -> g)
 *
 * This class is stateless and pure — no database access, just pure logic —
 * which makes it easy to test and easy to justify in the assignment video.
 */
public final class RecipeMatcher {

    private RecipeMatcher() { /* utility class */ }

    /**
     * Returns the list of recipes that the user can cook right now,
     * given their current pantry contents.
     */
    public static List<Recipe> findMatchingRecipes(List<Recipe> allRecipes,
                                                   List<PantryItem> pantry) {
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canCook(recipe, pantry)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /** True only if EVERY ingredient of the recipe is satisfied. */
    public static boolean canCook(Recipe recipe, List<PantryItem> pantry) {
        // Aggregate user's pantry by normalized ingredient name
        Map<String, Double> pantryAmounts = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = normalizeIngredient(item.getName());
            double canonical = toCanonicalQuantity(item.getQuantity(), item.getUnit());
            String unitType = unitType(item.getUnit());

            // Store as a compound key: "tomato|VOLUME" so different unit
            // families don't collide
            String compoundKey = key + "|" + unitType;
            pantryAmounts.merge(compoundKey, canonical, Double::sum);
        }

        // Every required ingredient must be satisfied
        for (RecipeIngredient required : recipe.getIngredients()) {
            String key = normalizeIngredient(required.getName());
            String type = unitType(required.getUnit());
            double needed = toCanonicalQuantity(required.getQuantity(), required.getUnit());

            Double have = pantryAmounts.get(key + "|" + type);
            if (have == null || have < needed) {
                return false;
            }
        }
        return true;
    }

    // ============== NORMALIZATION ==============

    /** Lowercase, trim, and de-pluralize a single word. */
    static String normalizeIngredient(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT).trim();

        // Handle irregulars first
        if (s.equals("tomatoes")) return "tomato";
        if (s.equals("potatoes")) return "potato";
        if (s.equals("children")) return "child";
        if (s.equals("flour")) return "flour";

        // Regular plural rules (order matters)
        if (s.endsWith("ies") && s.length() > 3) {
            return s.substring(0, s.length() - 3) + "y";     // berries -> berry
        }
        if (s.endsWith("ves") && s.length() > 3) {
            return s.substring(0, s.length() - 3) + "f";     // loaves -> loaf
        }
        if (s.endsWith("es") && s.length() > 2 &&
                (s.endsWith("ches") || s.endsWith("shes") ||
                 s.endsWith("sses") || s.endsWith("xes") || s.endsWith("zes"))) {
            return s.substring(0, s.length() - 2);            // boxes -> box
        }
        if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 1) {
            return s.substring(0, s.length() - 1);            // eggs -> egg
        }
        return s;
    }

    // ============== UNIT CONVERSION ==============

    /** Which measurement family a unit belongs to: MASS, VOLUME, or COUNT. */
    static String unitType(String unit) {
        if (unit == null) return "COUNT";
        String u = unit.toLowerCase(Locale.ROOT).trim();
        switch (u) {
            case "g": case "grams": case "gram": case "kg":
            case "mg":
                return "MASS";
            case "ml": case "milliliters": case "l": case "liters":
            case "litre": case "litres": case "cup": case "cups":
            case "tbsp": case "tsp":
                return "VOLUME";
            default:
                return "COUNT";
        }
    }

    /** Convert to canonical unit for the family: grams / ml / count. */
    static double toCanonicalQuantity(double quantity, String unit) {
        if (unit == null) return quantity;
        String u = unit.toLowerCase(Locale.ROOT).trim();
        switch (u) {
            // Mass family -> grams
            case "kg": return quantity * 1000.0;
            case "mg": return quantity / 1000.0;
            case "g": case "grams": case "gram": return quantity;

            // Volume family -> millilitres
            case "l": case "liter": case "liters": case "litre": case "litres":
                return quantity * 1000.0;
            case "cup": case "cups": return quantity * 240.0;   // 1 cup = 240ml
            case "tbsp": return quantity * 15.0;                 // 1 tbsp = 15ml
            case "tsp": return quantity * 5.0;                   // 1 tsp = 5ml
            case "ml": case "milliliters": case "millilitre":
                return quantity;

            // Count family is left as-is
            default: return quantity;
        }
    }
}
