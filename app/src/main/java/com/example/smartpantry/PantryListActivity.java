package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.database.PantryDataSource;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.OnItemClickListener, PantryAdapter.OnItemDeleteListener {

    private PantryDataSource dataSource;
    private PantryAdapter adapter;
    private final List<PantryItem> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView textEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        recyclerView = findViewById(R.id.recyclerViewPantry);
        textEmptyState = findViewById(R.id.textEmptyState);
        FloatingActionButton fab = findViewById(R.id.fabAddIngredient);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(items, this, this);
        recyclerView.setAdapter(adapter);

        dataSource = new PantryDataSource(this);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_recipes) {
                Intent i = new Intent(this, SuggestedRecipesActivity.class);
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
            return id == R.id.nav_pantry;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        dataSource.open();
        List<PantryItem> fresh = dataSource.getAllPantryItems();
        dataSource.close();

        items.clear();
        items.addAll(fresh);
        adapter.notifyDataSetChanged();

        if (items.isEmpty()) {
            textEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_PANTRY_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onItemDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.yes, (d, w) -> {
                    dataSource.open();
                    dataSource.deletePantryItem(item.getId());
                    dataSource.close();
                    loadPantry();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
