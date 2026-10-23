package com.example.smartpantry;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.database.RecipeDataSource;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra("RECIPE_ID", -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        RecipeDataSource dataSource = new RecipeDataSource(this);
        dataSource.open();
        Recipe recipe = dataSource.getRecipeById(recipeId);
        dataSource.close();

        if (recipe == null) {
            finish();
            return;
        }

        toolbar.setTitle(recipe.getName());

        TextView title = findViewById(R.id.textRecipeTitle);
        title.setText(recipe.getName());

        LinearLayout ingredientsContainer = findViewById(R.id.layoutIngredients);
        for (RecipeIngredient ing : recipe.getIngredients()) {
            TextView row = new TextView(this);
            String qty = formatQuantity(ing.getQuantity());
            row.setText("•  " + qty + " " + ing.getUnit() + " " + ing.getName());
            row.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_primary));
            row.setTextSize(15f);
            row.setPadding(0, 6, 0, 6);
            row.setGravity(Gravity.START);
            ingredientsContainer.addView(row);
        }

        TextView method = findViewById(R.id.textRecipeMethod);
        method.setText(recipe.getMethod());
    }

    private String formatQuantity(double q) {
        if (q == Math.floor(q)) return String.valueOf((long) q);
        return String.format(Locale.US, "%.1f", q);
    }
}
