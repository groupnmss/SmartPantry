package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.database.PantryDataSource;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_PANTRY_ID = "PANTRY_ID";
    public static final String[] UNITS = {
            "pieces", "grams", "kg", "ml", "liters", "cups", "tbsp", "tsp", "cloves", "pinch"
    };

    private PantryDataSource dataSource;
    private long pantryId = -1;

    private TextInputLayout layoutName, layoutQuantity;
    private TextInputEditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dataSource = new PantryDataSource(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        MaterialButton buttonCancel = findViewById(R.id.buttonCancel);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        editExpiry.setOnClickListener(v -> showDatePicker());

        if (getIntent().hasExtra(EXTRA_PANTRY_ID)) {
            pantryId = getIntent().getLongExtra(EXTRA_PANTRY_ID, -1);
            loadExisting(pantryId);
            setTitle("Edit Ingredient");
            toolbar.setTitle("Edit Ingredient");
        } else {
            setTitle("Add Ingredient");
        }

        buttonSave.setOnClickListener(v -> saveIngredient());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadExisting(long id) {
        dataSource.open();
        PantryItem item = dataSource.getPantryItem(id);
        dataSource.close();
        if (item == null) return;

        editName.setText(item.getName());
        editQuantity.setText(String.valueOf(item.getQuantity()));
        if (item.getExpiryDate() != null) editExpiry.setText(item.getExpiryDate());

        String unit = item.getUnit();
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(unit)) {
                spinnerUnit.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String formatted = String.format("%04d-%02d-%02d", year, month + 1, day);
                    editExpiry.setText(formatted);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveIngredient() {
        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String quantityStr = editQuantity.getText() != null ? editQuantity.getText().toString().trim() : "";

        layoutName.setError(null);
        layoutQuantity.setError(null);

        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            layoutQuantity.setError(getString(R.string.error_quantity_invalid));
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = editExpiry.getText() != null ? editExpiry.getText().toString().trim() : "";
        if (expiry.isEmpty()) expiry = null;

        dataSource.open();
        boolean success;
        if (pantryId == -1) {
            success = dataSource.insertPantryItem(
                    new PantryItem(name, quantity, unit, expiry)) > 0;
        } else {
            PantryItem updated = new PantryItem(pantryId, name, quantity, unit, expiry);
            success = dataSource.updatePantryItem(updated);
        }
        dataSource.close();

        if (success) {
            finish();
        } else {
            layoutName.setError("Failed to save. Please try again.");
        }
    }
}
