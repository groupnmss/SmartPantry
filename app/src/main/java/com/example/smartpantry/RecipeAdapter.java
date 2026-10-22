package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.model.Recipe;

import java.util.List;

/**
 * RecyclerView adapter for the Suggested Recipes screen.
 * Each item opens RecipeDetailActivity when tapped.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClickListener clickListener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener clickListener) {
        this.recipes = recipes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.textName.setText(recipe.getName());
        int n = recipe.getIngredients().size();
        holder.textIngredientCount.setText(n + (n == 1 ? " ingredient" : " ingredients"));
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onRecipeClick(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textIngredientCount;
        final TextView badgeMatch;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textIngredientCount = itemView.findViewById(R.id.textIngredientCount);
            badgeMatch = itemView.findViewById(R.id.badgeMatch);
        }
    }
}
