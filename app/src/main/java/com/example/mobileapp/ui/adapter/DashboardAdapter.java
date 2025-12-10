package com.example.mobileapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.DashboardEvent;
import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardVH> {

    public interface OnItemClick {
        void onClick(DashboardEvent item);
        void onEditClick(DashboardEvent item);
    }

    private final List<DashboardEvent> items = new ArrayList<>();
    private final OnItemClick listener;

    public DashboardAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void setItems(List<DashboardEvent> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DashboardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_event, parent, false);
        return new DashboardVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardVH holder, int position) {
        DashboardEvent item = items.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvTime.setText(item.start_time);
        holder.tvSold.setText("Sold: " + item.total_tickets_sold);
        holder.tvRevenue.setText("Revenue: " + item.total_revenue);
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DashboardVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvSold, tvRevenue;
        Button btnEdit;
        DashboardVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSold = itemView.findViewById(R.id.tvSold);
            tvRevenue = itemView.findViewById(R.id.tvRevenue);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}