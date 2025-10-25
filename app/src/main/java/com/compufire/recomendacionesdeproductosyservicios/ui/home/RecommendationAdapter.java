package com.compufire.recomendacionesdeproductosyservicios.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.VH> {

    // Interfaz para manejar clics en los ítems
    public interface OnItemClick {
        void onClick(Item item);
    }

    private final List<Item> data = new ArrayList<>();
    private final OnItemClick onItemClick;

    // Constructor vacío (sin listener)
    public RecommendationAdapter() {
        this(null);
    }

    // Constructor con listener (para HomeFragment → Detail)
    public RecommendationAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
        setHasStableIds(true); // optimiza el reciclado
    }

    public void submitList(List<Item> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        Item it = data.get(position);
        return (it != null) ? it.id : position;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item it = data.get(position);
        if (it == null) return;

        h.title.setText(it.nombre != null ? it.nombre : "—");
        String precio = String.format(Locale.getDefault(), "Q%.2f", it.precio);
        String categoria = it.categoria != null ? it.categoria : "—";
        h.subtitle.setText(categoria + " · " + precio);

        // Click en el ítem → callback
        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(it);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvNombre);
            subtitle = itemView.findViewById(R.id.tvCategoriaPrecio);
        }
    }
}



