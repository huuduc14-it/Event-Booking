package com.example.mobileapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull; // Good practice
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R; // Ensure R is imported
import com.example.mobileapp.data.model.TicketType;

import java.util.List;

public class ManageTicketAdapter extends RecyclerView.Adapter<ManageTicketAdapter.ViewHolder> {

    private List<TicketType> list;
    private OnTicketActionListener listener;

    public interface OnTicketActionListener {
        void onEdit(TicketType ticket);
        void onDelete(TicketType ticket);
    }

    public ManageTicketAdapter(List<TicketType> list, OnTicketActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_ticket, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TicketType t = list.get(position);
        holder.tvName.setText(t.name);
        holder.tvPrice.setText("$" + t.price);

        // Calculate Sold: Total - Remaining
        int sold = t.totalQuantity - t.remaining;
        holder.tvQty.setText("Sold: " + sold + " / " + t.totalQuantity);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(t));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(t));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty;
        ImageButton btnEdit, btnDelete;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTicketName);
            tvPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvQty = itemView.findViewById(R.id.tvTicketQty);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}