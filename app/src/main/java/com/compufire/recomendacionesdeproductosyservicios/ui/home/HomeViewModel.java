package com.compufire.recomendacionesdeproductosyservicios.ui.home;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.GeminiRepository;
import com.compufire.recomendacionesdeproductosyservicios.data.repository.RecommendationRepository;
import com.compufire.recomendacionesdeproductosyservicios.data.repository.UserPrefsRepository;
import com.compufire.recomendacionesdeproductosyservicios.util.RankingUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {
    public final MutableLiveData<List<Item>> items = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<String> explicacion = new MutableLiveData<>();

    private final RecommendationRepository recommendationRepository;
    private final UserPrefsRepository userPrefsRepository;
    private final GeminiRepository geminiRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public HomeViewModel(RecommendationRepository recommendationRepository,
                         UserPrefsRepository userPrefsRepository,
                         GeminiRepository geminiRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userPrefsRepository = userPrefsRepository;
        this.geminiRepository = geminiRepository;
    }

    public void cargarRecomendaciones(final String categoria, final Double min, final Double max, @Nullable final UserPrefs prefs) {
        loading.postValue(true);
        executor.execute(() -> {
            try {
                List<Item> lista;
                if (categoria != null && !categoria.isEmpty()) {
                    lista = recommendationRepository.getByCategoria(categoria);
                } else if (min != null && max != null) {
                    lista = recommendationRepository.getByPrecio(min, max);
                } else {
                    lista = recommendationRepository.getAll();
                }
                UserPrefs userPrefs = prefs != null ? prefs : userPrefsRepository.get();
                if (userPrefs == null) {
                    error.postValue("Preferencias de usuario no encontradas");
                    loading.postValue(false);
                    return;
                }
                // Simula distancia 0 (sin ubicación real)
                for (Item item : lista) {
                    // No se modifica el item, solo se usa para score
                }
                Collections.sort(lista, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        double s1 = RankingUtils.score(o1, userPrefs, 0);
                        double s2 = RankingUtils.score(o2, userPrefs, 0);
                        return Double.compare(s2, s1); // Descendente
                    }
                });
                items.postValue(new ArrayList<>(lista));
            } catch (Exception e) {
                error.postValue("Error al cargar recomendaciones");
            } finally {
                loading.postValue(false);
            }
        });
    }

    public void explicar(final Item item) {
        executor.execute(() -> {
            try {
                UserPrefs prefs = userPrefsRepository.get();
                String exp = geminiRepository.explicar(item, prefs);
                explicacion.postValue(exp);
            } catch (Exception e) {
                explicacion.postValue("No se pudo obtener explicación de IA.");
            }
        });
    }
}

