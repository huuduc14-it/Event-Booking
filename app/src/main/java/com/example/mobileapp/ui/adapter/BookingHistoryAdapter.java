package com.example.mobileapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.BookingHistoryItem;
import com.example.mobileapp.ui.activity.MyDetailTicketActivity; // Ensure this imports your QR Activity

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private Context context;
    private List<BookingHistoryItem> list;

    public BookingHistoryAdapter(Context context, List<BookingHistoryItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingHistoryItem item = list.get(position);

        holder.tvTitle.setText(item.eventTitle);
        holder.tvDateLoc.setText(item.startTime + " | " + item.locationName);
        holder.tvPrice.setText("$" + item.totalAmount);

        // Example format: "2 x VIP Ticket"
        holder.tvDetails.setText(item.quantity + " x " + item.ticketName);

        // HANDLE CLICK: OPEN QR CODE SCREEN
        holder.btnViewQr.setOnClickListener(v -> {
            Intent intent = new Intent(context, MyDetailTicketActivity.class);

            // Pass data to the QR Activity
            intent.putExtra("QR_CODE_STRING", item.qrCode);
            intent.putExtra("EVENT_TITLE", item.eventTitle);
            intent.putExtra("TICKET_NAME", item.ticketName);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateLoc, tvPrice, tvDetails;
        Button btnViewQr;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDateLoc = itemView.findViewById(R.id.tvDateLocation);
            tvPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvDetails = itemView.findViewById(R.id.tvTicketDetail);
            btnViewQr = itemView.findViewById(R.id.btnViewTicket);

        }
    }
}