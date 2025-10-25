package com.compufire.recomendacionesdeproductosyservicios.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.data.local.booking.BookingHistory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.VH> {

    private final List<BookingHistory> items = new ArrayList<>();

    public void setItems(List<BookingHistory> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BookingHistory b = items.get(position);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String date = df.format(new Date(b.createdAt));
        holder.tvDate.setText(date);
        holder.tvBusiness.setText(b.businessName != null ? b.businessName : b.businessId);
        holder.tvStatus.setText(b.status != null ? b.status : "-");
        holder.tvCode.setText(b.code != null ? b.code : "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvBusiness, tvStatus, tvCode;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBusiness = itemView.findViewById(R.id.tvBusiness);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCode = itemView.findViewById(R.id.tvCode);
        }
    }
}

