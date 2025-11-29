package com.example.mobileapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.TicketType;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<TicketType> list;
    private OnQuantityChangeListener listener;

    // Interface: Allows the Adapter to talk to the Activity
    // When a user clicks + or -, we tell the Activity: "Recalculate the Total Price now!"
    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public BookingAdapter(List<TicketType> list, OnQuantityChangeListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the row layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_booking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketType t = list.get(position);

        // 1. Set Text Data
        holder.tvName.setText(t.name);
        holder.tvPrice.setText("$" + t.price);
        holder.tvRemaining.setText(t.remaining + " available");
        holder.tvQty.setText(String.valueOf(t.selectedQty));

        // 2. Handle Plus Button
        holder.btnPlus.setOnClickListener(v -> {
            // Check if stock is available
            if (t.selectedQty < t.remaining) {
                t.selectedQty++; // Increase count

                // Update UI for this row
                notifyItemChanged(position);

                // Tell Activity to update Total Price
                listener.onQuantityChanged();
            } else {
                Toast.makeText(v.getContext(), "Max limit reached", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Handle Minus Button
        holder.btnMinus.setOnClickListener(v -> {
            if (t.selectedQty > 0) {
                t.selectedQty--; // Decrease count

                // Update UI for this row
                notifyItemChanged(position);

                // Tell Activity to update Total Price
                listener.onQuantityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 4. ViewHolder: Finds the IDs in your XML
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvRemaining, tvQty;
        ImageButton btnPlus, btnMinus;

        public ViewHolder(View itemView) {
            super(itemView);
            // These IDs must match 'item_ticket_booking.xml'
            tvName = itemView.findViewById(R.id.tvTicketName);
            tvPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvRemaining = itemView.findViewById(R.id.tvTicketRemaining);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}