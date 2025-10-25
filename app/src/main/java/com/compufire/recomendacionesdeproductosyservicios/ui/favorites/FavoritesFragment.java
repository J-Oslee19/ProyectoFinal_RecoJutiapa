package com.compufire.recomendacionesdeproductosyservicios.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.ui.home.RecommendationAdapter;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel vm;
    private RecommendationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // ⚡️ Importante: usamos el mismo layout que Home (con RecyclerView rvRecs)
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        RecyclerView rv = v.findViewById(R.id.rvRecs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Navegar al detalle cuando se toque un favorito
        NavController nav = NavHostFragment.findNavController(this);
        adapter = new RecommendationAdapter(item -> {
            Bundle args = new Bundle();
            args.putInt("itemId", item.id);
            nav.navigate(R.id.action_home_to_detail, args);
        });

        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(FavoritesViewModel.class);
        vm.getFavorites().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}

