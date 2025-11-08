package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.LocalVerticalAdapter;
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class LocalListActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewEmptyState;

    private FirebaseManager firebaseManager;
    private LocalVerticalAdapter adapter;
    private List<Local> localList;

    private String filterType;
    private String filterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_list);

        firebaseManager = new FirebaseManager();
        localList = new ArrayList<>();

        filterType = getIntent().getStringExtra("FILTER_TYPE");
        filterTitle = getIntent().getStringExtra("FILTER_TITLE");
        if (filterType == null) {
            filterType = "recentes";
            filterTitle = "Todos os Espaços";
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadLocais();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewLocaisLista);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
    }

    private void setupToolbar() {
        toolbar.setTitle(filterTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new LocalVerticalAdapter(this, localList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadLocais() {
        progressBar.setVisibility(View.VISIBLE);
        textViewEmptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        String sortBy = "timestamp";
        if (filterType.equals("populares")) {
            sortBy = "viewCount";
        } else if (filterType.equals("avaliados")) {
            sortBy = "rating";
        }

        firebaseManager.getLocaisOrdenados(sortBy, new FirebaseManager.LocaisCallback() {
            @Override
            public void onSuccess(List<Local> locais) {
                progressBar.setVisibility(View.GONE);
                localList.clear();
                localList.addAll(locais);
                adapter.updateData(localList);
                checkEmptyState();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LocalListActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                checkEmptyState();
            }
        });
    }

    private void checkEmptyState() {
        if (localList.isEmpty()) {
            textViewEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}