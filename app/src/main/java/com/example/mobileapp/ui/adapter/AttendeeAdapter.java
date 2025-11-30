package com.example.mobileapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Attendee;
import java.util.List;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.VH> {
    private final List<Attendee> items;
    public AttendeeAdapter(List<Attendee> items) { this.items = items; }

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
        holder.ticketInfo.setText(a.ticket_info != null ? a.ticket_info : "");
        holder.status.setText(a.status != null ? a.status : "");
    }

    @Override
    public int getItemCount() { return items == null ? 0 : items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, ticketInfo, status;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvAttendeeName);
            ticketInfo = itemView.findViewById(R.id.tvTicketInfo);
            status = itemView.findViewById(R.id.tvCheckInStatus);
        }
    }
}
