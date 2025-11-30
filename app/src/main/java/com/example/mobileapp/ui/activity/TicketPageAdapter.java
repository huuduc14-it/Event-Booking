package com.example.mobileapp.ui.activity;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mobileapp.ui.activity.TicketListFragment;

public class TicketPageAdapter extends FragmentStateAdapter {

    public TicketPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return TicketListFragment.newInstance(false); // Chưa diễn ra
        } else {
            return TicketListFragment.newInstance(true); // Đã diễn ra
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

