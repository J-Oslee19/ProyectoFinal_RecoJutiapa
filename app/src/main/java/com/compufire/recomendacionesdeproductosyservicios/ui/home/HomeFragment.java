package com.compufire.recomendacionesdeproductosyservicios.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private RecommendationAdapter adapter;
    private HomeRoomViewModel vm;
    private NavController nav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        rv = view.findViewById(R.id.rvRecs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // NavController
        nav = NavHostFragment.findNavController(this);

        // Adapter con click -> detalle
        adapter = new RecommendationAdapter(item -> {
            Bundle args = new Bundle();
            args.putInt("itemId", item.id);
            nav.navigate(R.id.action_home_to_detail, args);
        });
        rv.setAdapter(adapter);

        // ViewModel que ya filtra/ordena según UserPrefs
        vm = new ViewModelProvider(this).get(HomeRoomViewModel.class);
        vm.getItems().observe(getViewLifecycleOwner(), this::render);

        // Menú superior
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_open_favorites) {
                    nav.navigate(R.id.action_home_to_favorites);
                    return true;
                } else if (id == R.id.action_open_prefs) {
                    nav.navigate(R.id.action_home_to_prefs);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void render(List<Item> items) {
        if (items == null) return;
        adapter.submitList(items);
    }
}







