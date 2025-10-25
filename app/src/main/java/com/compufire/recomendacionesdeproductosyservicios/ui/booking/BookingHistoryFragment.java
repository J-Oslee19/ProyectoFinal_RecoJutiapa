package com.compufire.recomendacionesdeproductosyservicios.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.data.local.booking.BookingHistory;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;

import java.util.List;

public class BookingHistoryFragment extends Fragment {

    private RecyclerView rvBookings;
    private TextView tvEmpty;
    private RecyclerView.Adapter adapter; // usar tipo genérico para evitar errores de resolución

    public BookingHistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBookings = view.findViewById(R.id.rvBookings);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        BookingHistoryAdapter bha = new BookingHistoryAdapter();
        adapter = bha;
        rvBookings.setAdapter((RecyclerView.Adapter) adapter);

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            List<BookingHistory> list = db.bookingHistoryDao().getAll();
            requireActivity().runOnUiThread(() -> {
                if (list == null || list.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvBookings.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvBookings.setVisibility(View.VISIBLE);
                    // actualizar adapter casteando al tipo concreto
                    if (adapter instanceof BookingHistoryAdapter) {
                        ((BookingHistoryAdapter) adapter).setItems(list);
                    }
                }
            });
        }).start();
    }
}
