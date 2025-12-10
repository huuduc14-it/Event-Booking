package com.example.mobileapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Attendee;
import java.util.ArrayList;
import java.util.List;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.VH> {
    private final List<Attendee> items = new ArrayList<>();

    public void setItems(List<Attendee> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Attendee a = items.get(position);
        holder.name.setText(a.full_name != null ? a.full_name : "-");
        String ticketLine = a.ticket_type != null ? a.ticket_type : a.ticket_info;
        if (a.price > 0) {
            ticketLine = (ticketLine == null ? "" : ticketLine + " • ") + a.price;
        }
        holder.ticketInfo.setText(ticketLine != null ? ticketLine : "");
        String statusLine = a.qr_code != null ? ("QR: " + a.qr_code) : "";
        holder.status.setText(statusLine);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, ticketInfo, status;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvAttendeeName);
            ticketInfo = itemView.findViewById(R.id.tvTicketInfo);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}