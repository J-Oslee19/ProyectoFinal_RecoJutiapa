package com.compufire.recomendacionesdeproductosyservicios.ui.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.compufire.recomendacionesdeproductosyservicios.BuildConfig;
import com.compufire.recomendacionesdeproductosyservicios.R;
import com.compufire.recomendacionesdeproductosyservicios.data.local.booking.BookingHistory;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.GeminiRepository;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.booking.BookingRequest;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.booking.BookingResponse;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.booking.N8nRepository;
import com.compufire.recomendacionesdeproductosyservicios.util.RankingUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Detail screen with Favorite, "¿Por qué?" (Gemini) and "Reservar / Consultar" (n8n).
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    private DetailViewModel vm;
    private DetailFavoriteViewModel favVm;

    private TextView tvNombre, tvPrecio, tvCategoria, tvVendor;
    private RatingBar ratingBar;
    private ImageView ivImagen;
    private Button btnFavorite, btnWhyDetail;

    // Booking
    private Button btnBook;
    private ProgressBar progressBooking;

    private int itemId = -1;

    // Cache del item mostrado
    private Item currentItem;

    // Repo de Gemini
    private GeminiRepository geminiRepo;

    public DetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        // ---- UI ----
        tvNombre    = v.findViewById(R.id.tvNombre);
        tvPrecio    = v.findViewById(R.id.tvPrecio);
        tvCategoria = v.findViewById(R.id.tvCategoria);
        tvVendor    = v.findViewById(R.id.tvVendor);
        ratingBar   = v.findViewById(R.id.ratingBar);
        ivImagen    = v.findViewById(R.id.ivImagen);
        btnFavorite = v.findViewById(R.id.btnFavorite);
        btnWhyDetail= v.findViewById(R.id.btnWhyDetail);

        btnBook = v.findViewById(R.id.btnBook);
        progressBooking = v.findViewById(R.id.progressBooking);

        // id del item recibido desde Home
        itemId = (getArguments() != null) ? getArguments().getInt("itemId", -1) : -1;

        // ---- ViewModels ----
        vm = new ViewModelProvider(this).get(DetailViewModel.class);
        favVm = new ViewModelProvider(this).get(DetailFavoriteViewModel.class);

        // Repo Gemini
        geminiRepo = new GeminiRepository();

        // Observa el Item
        vm.load(itemId).observe(getViewLifecycleOwner(), this::render);

        // Observa estado de favorito
        favVm.getIsFavorite().observe(getViewLifecycleOwner(), isFav -> {
            if (isFav != null && isFav) {
                btnFavorite.setText(getString(R.string.remove_favorite));
            } else {
                btnFavorite.setText(getString(R.string.add_favorite));
            }
        });

        // Consulta inicial: ¿ya es favorito?
        favVm.checkIfFavorite(itemId);

        // Click para alternar favorito
        btnFavorite.setOnClickListener(view -> {
            Boolean isFav = favVm.getIsFavorite().getValue();
            String itemName = currentItem != null ? currentItem.nombre : null;
            if (isFav != null && isFav) {
                favVm.removeFavorite(itemId, itemName);
            } else {
                favVm.addFavorite(itemId, itemName);
            }
        });

        // Diagnóstico rápido de Gemini Key (opcional)
        Log.d("GeminiKey", "len=" + (BuildConfig.GEMINI_API_KEY != null ? BuildConfig.GEMINI_API_KEY.length() : 0));

        // Botón “¿Por qué?”
        btnWhyDetail.setOnClickListener(v1 -> onClickPorQue());

        // Botón Reservar / Consultar
        btnBook.setOnClickListener(view -> onClickReservar(view));
    }

    // ----------------- RESERVAR / CONSULTAR -----------------

    private void onClickReservar(View anchor) {
        if (!isAdded()) return;
        if (currentItem == null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.reservar_consultar))
                    .setMessage(getString(R.string.no_item_loaded))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        showDatePickerAndTime(anchor);
    }

    private void showDatePickerAndTime(View anchor) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            TimePickerDialog tp = new TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                // Construir ISO-8601 con offset local (compatible API baja)
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                String iso = toIsoOffset(cal.getTime());

                // Crear BookingRequest
                BookingRequest.User user = new BookingRequest.User("u_1", "Angel", "acermenog@miumg.edu.gt");
                // Para "business", enviamos info del proveedor del item
                BookingRequest.Business business = new BookingRequest.Business(
                        String.valueOf(currentItem.vendorId),
                        "Proveedor: " + currentItem.vendorId
                );
                String reqId = "req-" + UUID.randomUUID();

                BookingRequest req = new BookingRequest(
                        reqId, user, business, "RESERVA", iso, "Reservado desde app");

                progressBooking.setVisibility(View.VISIBLE);

                // Llamada en background + guardas de configuración
                Executors.newSingleThreadExecutor().execute(() -> {
                    N8nRepository repo = new N8nRepository();
                    BookingResponse resp = null;
                    Exception error = null;
                    try {
                        // Validaciones para evitar crash si no hay config
                        if (BuildConfig.N8N_BASE_URL == null || BuildConfig.N8N_BASE_URL.trim().isEmpty())
                            throw new IOException("N8N_BASE_URL vacío (configura local.properties y sincroniza Gradle)");
                        if (BuildConfig.SHARED_SECRET == null || BuildConfig.SHARED_SECRET.trim().isEmpty())
                            throw new IOException("SHARED_SECRET vacío (configura local.properties)");

                        resp = repo.send(req);
                    } catch (Exception e) {
                        error = e;
                    }

                    // Registrar intento en BD (background)
                    try {
                        BookingHistory bh = new BookingHistory(
                                req.requestId,
                                business.id,
                                business.name,
                                req.type,
                                req.datetime,
                                (resp != null && resp.status != null) ? resp.status :
                                        (error != null ? "ERROR" : "UNKNOWN"),
                                (resp != null && resp.code != null) ? resp.code :
                                        (error != null ? "ERR" : ""),
                                System.currentTimeMillis()
                        );
                        AppDatabase db = AppDatabase.getInstance(requireContext());
                        db.bookingHistoryDao().insert(bh);
                    } catch (Throwable t) {
                        Log.w(TAG, "No se pudo registrar en BookingHistory", t);
                    }

                    final BookingResponse finalResp = resp;
                    final Exception finalError = error;

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        progressBooking.setVisibility(View.GONE);

                        if (finalError != null) {
                            String msg = (finalError.getMessage() != null)
                                    ? finalError.getMessage()
                                    : finalError.getClass().getSimpleName();
                            Snackbar.make(anchor, "Error al reservar: " + msg, Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        if (finalResp != null) {
                            String msg = "Estado: " + (finalResp.status != null ? finalResp.status : "-")
                                    + " • Código: " + (finalResp.code != null ? finalResp.code : "");
                            Snackbar snackbar = Snackbar.make(anchor, msg, Snackbar.LENGTH_LONG);
                            snackbar.setAction(getString(R.string.ver_historial), v -> {
                                try {
                                    int destId = requireContext().getResources()
                                            .getIdentifier("bookingHistoryFragment", "id", requireContext().getPackageName());
                                    if (destId != 0) {
                                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                                .navigate(destId);
                                    } else {
                                        Log.w(TAG, "bookingHistoryFragment id not found");
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "Navigation error", ex);
                                }
                            });
                            snackbar.show();
                        } else {
                            Snackbar.make(anchor, getString(R.string.respuesta_vacia_servidor), Snackbar.LENGTH_LONG).show();
                        }
                    });
                });

            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            tp.show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    /** yyyy-MM-dd'T'HH:mm:ss±HH:mm – compatible con API baja (sin java.time) */
    private String toIsoOffset(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
        return sdf.format(date);
    }

    // ----------------- ¿POR QUÉ? (Gemini) -----------------

    private void onClickPorQue() {
        if (!isAdded()) return;
        if (getActivity() == null || getActivity().isFinishing()) return;

        if (currentItem == null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.por_que_title))
                    .setMessage(getString(R.string.no_item_loaded))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        // Baseline simple si no lees prefs reales aquí.
        UserPrefs prefs = new UserPrefs();
        prefs.presupuestoMin = 0;
        prefs.presupuestoMax = 999_999;

        AlertDialog loading = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.consultando_gemini))
                .setMessage(getString(R.string.loading_message))
                .setCancelable(false)
                .create();
        loading.show();

        new Thread(() -> {
            String texto;
            try {
                texto = geminiRepo.explicar(currentItem, prefs);
                if (texto == null || texto.trim().isEmpty()) {
                    texto = RankingUtils.explicarCorto(currentItem, prefs, 0);
                }
            } catch (Exception e) {
                texto = RankingUtils.explicarCorto(currentItem, prefs, 0);
            }

            final String finalTexto = texto;

            if (!isAdded() || getActivity() == null || getActivity().isFinishing()) return;
            requireActivity().runOnUiThread(() -> {
                if (loading.isShowing()) loading.dismiss();

                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.por_que_title))
                        .setMessage(finalTexto)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            });
        }).start();
    }

    // ----------------- Render UI -----------------

    private void render(@Nullable Item it) {
        currentItem = it;
        if (it == null) return;

        tvNombre.setText(it.nombre != null ? it.nombre : "—");
        tvCategoria.setText(it.categoria != null ? it.categoria : "—");
        tvPrecio.setText(String.format(Locale.getDefault(), "Q%.2f", it.precio));
        try { ratingBar.setRating((float) it.rating); } catch (Throwable ignored) {}
        tvVendor.setText(getString(R.string.vendor_id_format, it.vendorId));

        Glide.with(this)
                .load(it.imagenUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivImagen);
    }
}
