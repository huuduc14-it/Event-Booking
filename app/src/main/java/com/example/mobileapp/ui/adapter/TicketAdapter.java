package com.example.mobileapp.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Ticket;

import java.util.List;

//public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
//
//    private Context context;
//    private List<Ticket> ticketList;
//
//    public TicketAdapter(Context context, List<Ticket> ticketList) {
//        this.context = context;
//        this.ticketList = ticketList;
//    }
//
//    @NonNull
//    @Override
//    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context)
//                .inflate(R.layout.item_ticket, parent, false);
//        return new TicketViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
//        Ticket ticket = ticketList.get(position);
//
//        holder.tvEventName.setText(ticket.getEventTitle());
//        holder.tvSeat.setText(ticket.getTicketTypeName()); // loại vé
//        holder.tvDate.setText(ticket.getStartTime());
//        holder.tvPrice.setText(ticket.getPrice() + " VNĐ");
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return ticketList.size();
//    }
//
//    public static class TicketViewHolder extends RecyclerView.ViewHolder {
//
//        TextView tvEventName, tvSeat, tvDate, tvPrice;
//
//        public TicketViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvEventName = itemView.findViewById(R.id.tvEventName);
//            tvSeat = itemView.findViewById(R.id.tvSeat);
//            tvDate = itemView.findViewById(R.id.tvDate);
//            tvPrice = itemView.findViewById(R.id.tvPrice);
//        }
//    }
//}
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> ticketList;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public void setOnTicketClickListener(OnTicketClickListener listener) {
        this.listener = listener;
    }

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.tvEventName.setText(ticket.getEventTitle());
        holder.tvSeat.setText(ticket.getTicketTypeName());
        holder.tvDate.setText(ticket.getStartTime());
        holder.tvPrice.setText(ticket.getPrice() + " VNĐ");

        // Xử lý nhấn vào vé
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTicketClick(ticket);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvSeat, tvDate, tvPrice;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}

