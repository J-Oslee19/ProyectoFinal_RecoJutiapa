package com.compufire.recomendacionesdeproductosyservicios.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.compufire.recomendacionesdeproductosyservicios.R;

public class OnboardingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnContinuar = view.findViewById(R.id.btnContinuar);
        btnContinuar.setOnClickListener(v ->
                        // Usa la acción si la tienes definida:
                        Navigation.findNavController(view)
                                .navigate(R.id.action_onboardingFragment_to_homeFragment)
                // Si prefieres, puedes dejar la línea siguiente y quitar la de arriba:
                // Navigation.findNavController(view).navigate(R.id.homeFragment)
        );
    }
}