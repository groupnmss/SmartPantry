package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.database.PantryDataSource;
import com.example.smartpantry.database.RecipeDataSource;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.RecipeMatcher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textEmptyState;
    private RecipeAdapter adapter;
    private final List<Recipe> matches = new ArrayList<>();
    private PantryDataSource pantryDataSource;
    private RecipeDataSource recipeDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerViewRecipes);
        textEmptyState = findViewById(R.id.textEmptyState);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(matches, this::openRecipeDetail);
        recyclerView.setAdapter(adapter);

        pantryDataSource = new PantryDataSource(this);
        recipeDataSource = new RecipeDataSource(this);

        bottomNav.setSelectedItemId(R.id.nav_recipes);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                Intent i = new Intent(this, PantryListActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                Intent i = new Intent(this, SettingsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;
            }
            return id == R.id.nav_recipes;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMatches();
    }

    private void refreshMatches() {
        pantryDataSource.open();
        List<PantryItem> pantry = pantryDataSource.getAllPantryItems();
        pantryDataSource.close();

        recipeDataSource.open();
        List<Recipe> allRecipes = recipeDataSource.getAllRecipesWithIngredients();
        recipeDataSource.close();

        List<Recipe> newMatches = RecipeMatcher.findMatchingRecipes(allRecipes, pantry);

        matches.clear();
        matches.addAll(newMatches);
        adapter.notifyDataSetChanged();

        if (matches.isEmpty()) {
            textEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("RECIPE_ID", recipe.getId());
        startActivity(intent);
    }
}
