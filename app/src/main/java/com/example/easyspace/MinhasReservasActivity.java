package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.ReservaAdapter;
import com.example.easyspace.models.Reserva;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MinhasReservasActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewReservas;
    private ReservaAdapter adapter;
    private List<Reserva> reservaListTotal;
    private ProgressBar progressBar;
    private TextView textViewEmptyState;
    private FirebaseManager firebaseManager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_reservas);

        firebaseManager = new FirebaseManager();
        initViews();
        setupToolbar();
        setupBottomNavigation();
        setupRecyclerView();
        setupTabLayout();
        loadReservas();
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerViewReservas = findViewById(R.id.recyclerViewReservas);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.getMenu().findItem(R.id.nav_reservations).setChecked(true);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_criar_anuncio) {
                if (firebaseManager.isLoggedIn()) {
                    startActivity(new Intent(this, CriarAnuncioActivity.class));
                } else {
                    Toast.makeText(this, "Você precisa estar logado para criar um anúncio.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
            } else if (itemId == R.id.nav_reservations) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (firebaseManager.isLoggedIn()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
            }
            return false;
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Minhas Reservas");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        reservaListTotal = new ArrayList<>();
        adapter = new ReservaAdapter(this, new ArrayList<>());
        recyclerViewReservas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReservas.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Confirmadas"));
        tabLayout.addTab(tabLayout.newTab().setText("Pendentes"));
        tabLayout.addTab(tabLayout.newTab().setText("Canceladas"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterReservasByStatus(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterReservasByStatus(int tabPosition) {
        if (reservaListTotal == null || reservaListTotal.isEmpty()) {
            textViewEmptyState.setText("Você ainda não possui reservas");
            textViewEmptyState.setVisibility(View.VISIBLE);
            recyclerViewReservas.setVisibility(View.GONE);
            return;
        }

        String status;
        switch (tabPosition) {
            case 0:
                status = "confirmed";
                break;
            case 1:
                status = "pending";
                break;
            case 2:
                status = "cancelled";
                break;
            default:
                status = "confirmed";
        }

        final String finalStatus = status;
        List<Reserva> reservasFiltradas = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            reservasFiltradas = reservaListTotal.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus().equals(finalStatus))
                    .collect(Collectors.toList());
        } else {
            for (Reserva r : reservaListTotal) {
                if (r.getStatus() != null && r.getStatus().equals(finalStatus)) {
                    reservasFiltradas.add(r);
                }
            }
        }


        adapter.updateData(reservasFiltradas);

        if (reservasFiltradas.isEmpty()) {
            textViewEmptyState.setText("Nenhuma reserva encontrada para este status");
            textViewEmptyState.setVisibility(View.VISIBLE);
            recyclerViewReservas.setVisibility(View.GONE);
        } else {
            textViewEmptyState.setVisibility(View.GONE);
            recyclerViewReservas.setVisibility(View.VISIBLE);
        }
    }


    private void loadReservas() {
        if (!firebaseManager.isLoggedIn()) {
            Toast.makeText(this, "Faça login para ver suas reservas", Toast.LENGTH_SHORT).show();
            textViewEmptyState.setText("Faça login para ver suas reservas");
            textViewEmptyState.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textViewEmptyState.setVisibility(View.GONE);
        recyclerViewReservas.setVisibility(View.GONE);

        firebaseManager.getUserReservas(new FirebaseManager.ReservasCallback() {
            @Override
            public void onSuccess(List<Reserva> reservas) {
                progressBar.setVisibility(View.GONE);
                if (reservas.isEmpty()) {
                    textViewEmptyState.setText("Você ainda não possui reservas");
                    textViewEmptyState.setVisibility(View.VISIBLE);
                } else {
                    reservaListTotal.clear();
                    reservaListTotal.addAll(reservas);
                    filterReservasByStatus(tabLayout.getSelectedTabPosition());
                }
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                textViewEmptyState.setText("Erro ao carregar reservas");
                textViewEmptyState.setVisibility(View.VISIBLE);
                Toast.makeText(MinhasReservasActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}