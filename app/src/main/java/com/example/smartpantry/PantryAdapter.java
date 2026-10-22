package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.model.PantryItem;

import java.util.List;

/**
 * RecyclerView adapter for the pantry list screen.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClickListener clickListener;
    private final OnItemDeleteListener deleteListener;

    public PantryAdapter(List<PantryItem> items,
                         OnItemClickListener clickListener,
                         OnItemDeleteListener deleteListener) {
        this.items = items;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.textName.setText(item.getName());
        holder.textQuantity.setText(formatQuantity(item));

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setVisibility(View.VISIBLE);
            holder.textExpiry.setText("Expires: " + item.getExpiryDate());
        } else {
            holder.textExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(item);
        });
        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onItemDelete(item);
        });
    }

    private String formatQuantity(PantryItem item) {
        double q = item.getQuantity();
        String qtyStr = (q == Math.floor(q))
                ? String.valueOf((long) q)
                : String.valueOf(q);
        return qtyStr + " " + item.getUnit();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;
        final ImageButton buttonDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textIngredientName);
            textQuantity = itemView.findViewById(R.id.textIngredientQuantity);
            textExpiry = itemView.findViewById(R.id.textIngredientExpiry);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
