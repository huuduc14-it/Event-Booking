package com.example.mobileapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Ticket;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.TicketResponse;
import com.example.mobileapp.ui.adapter.TicketAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//public class TicketListFragment extends Fragment {
//
//    private static final String ARG_PAST = "isPast";
//    private boolean isPast;
//    private RecyclerView recyclerView;
//    private TicketAdapter adapter;
//    private List<Ticket> ticketList = new ArrayList<>();
//
//    public static TicketListFragment newInstance(boolean isPast) {
//        TicketListFragment fragment = new TicketListFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_PAST, isPast);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if(getArguments() != null){
//            isPast = getArguments().getBoolean(ARG_PAST);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);
//
//        recyclerView = view.findViewById(R.id.rvTickets);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new TicketAdapter(getContext(), ticketList);
//        recyclerView.setAdapter(adapter);
//
//        loadTickets();
//
//        return view;
//    }
//
//    private void loadTickets() {
//        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
//
//        api.getTicketsByUser("Bearer " + token).enqueue(new Callback<List<Ticket>>() {
//            @Override
//            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
//                if(response.isSuccessful() && response.body() != null){
//                    ticketList.clear();
//                    for(Ticket t : response.body()){
//                        boolean ended = t.isPast(); // thêm method isPast() trong model Ticket
//                        if((isPast && ended) || (!isPast && !ended)){
//                            ticketList.add(t);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Ticket>> call, Throwable t) {
//                Log.e("API_ERROR", t.getMessage());
//                Toast.makeText(getContext(), "Không tải được vé", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
public class TicketListFragment extends Fragment {

    private static final String ARG_PAST = "isPast";
    private boolean isPast;
    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<Ticket> ticketList = new ArrayList<>();

    public static TicketListFragment newInstance(boolean isPast) {
        TicketListFragment fragment = new TicketListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PAST, isPast);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            isPast = getArguments().getBoolean(ARG_PAST);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        recyclerView = view.findViewById(R.id.rvTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TicketAdapter(getContext(), ticketList);
        recyclerView.setAdapter(adapter);
        adapter.setOnTicketClickListener(ticket -> {
            if (ticket.isPast()) {
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                intent.putExtra("event_id", ticket.getEventId());
                intent.putExtra("event_title", ticket.getEventTitle());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Sự kiện chưa diễn ra", Toast.LENGTH_SHORT).show();
            }
        });

        loadTickets();

        return view;
    }

    private void loadTickets() {
        // Lấy token từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if(token == null){
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getTicketsByUser("Bearer " + token).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    ticketList.clear();
                    for(Ticket t : response.body().getTickets()){
                        // Lọc vé theo trạng thái
                        if((isPast && t.isPast()) || (!isPast && !t.isPast())){
                            ticketList.add(t);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không tải được vé", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

