package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.LocalAdapter;
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;
import com.example.easyspace.utils.SampleDataGenerator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewAnuncios;
    private ProgressBar progressBar;
    private TextView textViewEmptyState;
    private Button buttonGenerateSamples;
    private LocalAdapter adapter;
    private FirebaseManager firebaseManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        firebaseManager = new FirebaseManager();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadUserListings();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewAnuncios = findViewById(R.id.recyclerViewAnuncios);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
        buttonGenerateSamples = findViewById(R.id.buttonGenerateSamples);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meus Anúncios");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerViewAnuncios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocalAdapter(this, new ArrayList<>());
        recyclerViewAnuncios.setAdapter(adapter);

        buttonGenerateSamples.setOnClickListener(v -> generateSampleData());
    }

    private void generateSampleData() {
        progressBar.setVisibility(View.VISIBLE);
        buttonGenerateSamples.setEnabled(false);
        textViewEmptyState.setVisibility(View.GONE);
        TextView textViewEmptySubtitle = findViewById(R.id.textViewEmptySubtitle);
        textViewEmptySubtitle.setVisibility(View.GONE);

        SampleDataGenerator.generateAndSaveSampleData(new SampleDataGenerator.GenerateCallback() {
            @Override
            public void onSuccess(int count) {
                progressBar.setVisibility(View.GONE);
                buttonGenerateSamples.setVisibility(View.GONE);
                Toast.makeText(MeusAnunciosActivity.this,
                        count + " locais de exemplo criados com sucesso!", Toast.LENGTH_LONG).show();
                loadUserListings();
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                buttonGenerateSamples.setEnabled(true);
                textViewEmptyState.setVisibility(View.VISIBLE);
                textViewEmptySubtitle.setVisibility(View.VISIBLE);
                Toast.makeText(MeusAnunciosActivity.this,
                        "Erro ao gerar dados: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadUserListings() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            textViewEmptyState.setVisibility(View.VISIBLE);
            textViewEmptyState.setText("Faça login para ver seus anúncios");
            TextView textViewEmptySubtitle = findViewById(R.id.textViewEmptySubtitle);
            textViewEmptySubtitle.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("locais")
                .whereEqualTo("proprietarioId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        List<Local> userLocations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Local local = document.toObject(Local.class);
                            if (local.getId() == null) {
                                local.setId(document.getId());
                            }
                            userLocations.add(local);
                        }

                        TextView textViewEmptySubtitle = findViewById(R.id.textViewEmptySubtitle);

                        if (userLocations.isEmpty()) {
                            textViewEmptyState.setVisibility(View.VISIBLE);
                            textViewEmptySubtitle.setVisibility(View.VISIBLE);
                            buttonGenerateSamples.setVisibility(View.VISIBLE);
                            recyclerViewAnuncios.setVisibility(View.GONE);
                        } else {
                            textViewEmptyState.setVisibility(View.GONE);
                            textViewEmptySubtitle.setVisibility(View.GONE);
                            buttonGenerateSamples.setVisibility(View.GONE);
                            recyclerViewAnuncios.setVisibility(View.VISIBLE);
                            adapter.updateData(userLocations);
                        }
                    } else {
                        textViewEmptyState.setVisibility(View.VISIBLE);
                        textViewEmptyState.setText("Erro ao carregar anúncios");
                        TextView textViewEmptySubtitle = findViewById(R.id.textViewEmptySubtitle);
                        textViewEmptySubtitle.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserListings();
    }
}
