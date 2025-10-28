package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.ReservaAdapter;
import com.example.easyspace.models.Reserva;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MinhasReservasActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewReservas;
    private ProgressBar progressBar;
    private TextView textViewEmptyState;
    private ReservaAdapter adapter;
    private FirebaseManager firebaseManager;
    private FirebaseFirestore db;
    private String currentFilter = "todas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_reservas);

        firebaseManager = new FirebaseManager();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();
        loadReservations();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerViewReservas = findViewById(R.id.recyclerViewReservas);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Minhas Reservas");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Todas"));
        tabLayout.addTab(tabLayout.newTab().setText("Pendentes"));
        tabLayout.addTab(tabLayout.newTab().setText("Confirmadas"));
        tabLayout.addTab(tabLayout.newTab().setText("Concluídas"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentFilter = "todas";
                        break;
                    case 1:
                        currentFilter = "pendente";
                        break;
                    case 2:
                        currentFilter = "confirmada";
                        break;
                    case 3:
                        currentFilter = "concluida";
                        break;
                }
                loadReservations();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        recyclerViewReservas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservaAdapter(this, new ArrayList<>());
        recyclerViewReservas.setAdapter(adapter);
    }

    private void loadReservations() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            textViewEmptyState.setVisibility(View.VISIBLE);
            textViewEmptyState.setText("Faça login para ver suas reservas");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textViewEmptyState.setVisibility(View.GONE);
        recyclerViewReservas.setVisibility(View.GONE);

        db.collection("reservas")
                .whereEqualTo("usuarioId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        List<Reserva> reservas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reserva reserva = document.toObject(Reserva.class);
                            if (reserva.getId() == null) {
                                reserva.setId(document.getId());
                            }

                            if (currentFilter.equals("todas") ||
                                    reserva.getStatus().equals(currentFilter)) {
                                reservas.add(reserva);
                            }
                        }

                        if (reservas.isEmpty()) {
                            textViewEmptyState.setVisibility(View.VISIBLE);
                            textViewEmptyState.setText(getEmptyStateMessage());
                            recyclerViewReservas.setVisibility(View.GONE);
                        } else {
                            textViewEmptyState.setVisibility(View.GONE);
                            recyclerViewReservas.setVisibility(View.VISIBLE);
                            adapter.updateData(reservas);
                        }
                    } else {
                        textViewEmptyState.setVisibility(View.VISIBLE);
                        textViewEmptyState.setText("Erro ao carregar reservas");
                    }
                });
    }

    private String getEmptyStateMessage() {
        switch (currentFilter) {
            case "pendente":
                return "Nenhuma reserva pendente";
            case "confirmada":
                return "Nenhuma reserva confirmada";
            case "concluida":
                return "Nenhuma reserva concluída";
            default:
                return "Você ainda não tem reservas";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }
}
