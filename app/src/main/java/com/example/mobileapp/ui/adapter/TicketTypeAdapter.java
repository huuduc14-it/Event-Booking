package com.example.mobileapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.network.dto.TicketTypeResponse;
import java.util.ArrayList;
import java.util.List;

public class TicketTypeAdapter extends RecyclerView.Adapter<TicketTypeAdapter.TicketTypeVH> {

    public interface OnUpdateListener {
        void onUpdate(int ticketTypeId, int newQuantity);
    }

    private final List<TicketTypeResponse.TicketType> items = new ArrayList<>();
    private final OnUpdateListener listener;

    public TicketTypeAdapter(OnUpdateListener listener) {
        this.listener = listener;
    }

    public void setItems(List<TicketTypeResponse.TicketType> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketTypeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_type, parent, false);
        return new TicketTypeVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketTypeVH holder, int position) {
        TicketTypeResponse.TicketType item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvPrice.setText(String.format("Giá: %,.0f VND", item.price));
        holder.tvCurrent.setText(String.format("Hiện tại: %d / %d vé", item.remaining, item.total_quantity));
        holder.tvSold.setText(String.format("Đã bán: %d vé", item.total_quantity - item.remaining));

        if (item.description != null && !item.description.isEmpty()) {
            holder.tvDesc.setText(item.description);
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        holder.etNewQuantity.setText(String.valueOf(item.total_quantity));

        holder.btnUpdate.setOnClickListener(v -> {
            String newQtyStr = holder.etNewQuantity.getText().toString().trim();
            if (!newQtyStr.isEmpty()) {
                try {
                    int newQty = Integer.parseInt(newQtyStr);
                    listener.onUpdate(item.ticket_type_id, newQty);
                } catch (NumberFormatException e) {
                    holder.etNewQuantity.setError("Số không hợp lệ");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TicketTypeVH extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCurrent, tvSold, tvDesc;
        EditText etNewQuantity;
        Button btnUpdate;

        TicketTypeVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTicketName);
            tvPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvCurrent = itemView.findViewById(R.id.tvTicketCurrent);
            tvSold = itemView.findViewById(R.id.tvTicketSold);
            tvDesc = itemView.findViewById(R.id.tvTicketDesc);
            etNewQuantity = itemView.findViewById(R.id.etNewQuantity);
            btnUpdate = itemView.findViewById(R.id.btnUpdateQuantity);
        }
    }
}