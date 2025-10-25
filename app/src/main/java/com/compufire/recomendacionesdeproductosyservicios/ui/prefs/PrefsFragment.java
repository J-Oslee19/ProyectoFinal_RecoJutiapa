package com.compufire.recomendacionesdeproductosyservicios.ui.prefs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;
import com.compufire.recomendacionesdeproductosyservicios.data.repository.UserPrefsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Formulario simple para editar y guardar UserPrefs en Room usando UserPrefsRepository.
 * - Carga valores guardados al abrir.
 * - Guarda al tocar "Guardar" y vuelve atrás.
 */
public class PrefsFragment extends Fragment {

    private EditText etMin, etMax;
    private CheckBox cbComida, cbServicios, cbTecnologia;
    private Button btnSave;

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private UserPrefsRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prefs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        // UI
        etMin = v.findViewById(R.id.etMin);
        etMax = v.findViewById(R.id.etMax);
        cbComida = v.findViewById(R.id.cbComida);
        cbServicios = v.findViewById(R.id.cbServicios);
        cbTecnologia = v.findViewById(R.id.cbTecnologia);
        btnSave = v.findViewById(R.id.btnGuardarPrefs);

        // Repo (usa el DAO interno)
        repo = new UserPrefsRepository(
                AppDatabase.getInstance(requireContext()).userPrefsDao()
        );

        // Cargar prefs guardadas (si existen)
        io.execute(() -> {
            UserPrefs prefs = repo.get();
            if (prefs == null) return; // primera vez, dejar vacíos
            requireActivity().runOnUiThread(() -> {
                etMin.setText(String.valueOf(prefs.presupuestoMin));
                etMax.setText(String.valueOf(prefs.presupuestoMax));
                if (prefs.categorias != null) {
                    cbComida.setChecked(prefs.categorias.contains("Comida"));
                    cbServicios.setChecked(prefs.categorias.contains("Servicios"));
                    cbTecnologia.setChecked(prefs.categorias.contains("Tecnología"));
                }
            });
        });

        // Guardar
        btnSave.setOnClickListener(view -> {
            double min = parseDoubleSafe(etMin.getText().toString(), 0);
            double max = parseDoubleSafe(etMax.getText().toString(), 9999);

            if (max < min) {
                Toast.makeText(requireContext(),
                        "El máximo no puede ser menor que el mínimo",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> categorias = new ArrayList<>();
            if (cbComida.isChecked()) categorias.add("Comida");
            if (cbServicios.isChecked()) categorias.add("Servicios");
            if (cbTecnologia.isChecked()) categorias.add("Tecnología");

            UserPrefs prefs = new UserPrefs();
            prefs.id = 1; // clave fija siempre 1
            prefs.presupuestoMin = min;
            prefs.presupuestoMax = max;
            prefs.categorias = categorias;
            prefs.usaUbicacion = false;

            io.execute(() -> {
                // 👉 ahora guardamos vía REPO (que llama upsert en el Dao)
                repo.save(prefs);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Preferencias guardadas",
                            Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).navigateUp();
                });
            });
        });
    }

    private double parseDoubleSafe(String s, double def) {
        if (TextUtils.isEmpty(s)) return def;
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
}



