package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.CategoriaAdapter;
import com.example.easyspace.adapters.LocalAdapter;
import com.example.easyspace.models.Categoria;
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPesquisa;
    private RecyclerView recyclerViewCategorias, recyclerViewLocais;
    private RecyclerView recyclerViewRecentes, recyclerViewPopulares, recyclerViewMelhoresAvaliacoes;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabCriarAnuncio;
    private ProgressBar progressBar;
    private TextView textViewEmptyState;

    private CategoriaAdapter categoriaAdapter;
    private LocalAdapter locaisAdapter, recentesAdapter, popularesAdapter, melhoresAvaliacoesAdapter;

    private FirebaseManager firebaseManager;
    private FirebaseFirestore db;
    private List<Local> allLocais;
    private String selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseManager = new FirebaseManager();
        db = FirebaseFirestore.getInstance();
        initViews();
        setupRecyclerViews();
        setupBottomNavigation();
        setupSearch();
        setupFab();
        loadCategorias();

        if (getIntent().getBooleanExtra("focusSearch", false)) {
            editTextPesquisa.requestFocus();
        }
    }

    private void initViews() {
        editTextPesquisa = findViewById(R.id.editTextPesquisa);
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recyclerViewLocais = findViewById(R.id.recyclerViewLocais);
        recyclerViewRecentes = findViewById(R.id.recyclerViewRecentes);
        recyclerViewPopulares = findViewById(R.id.recyclerViewPopulares);
        recyclerViewMelhoresAvaliacoes = findViewById(R.id.recyclerViewMelhoresAvaliacoes);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabCriarAnuncio = findViewById(R.id.fabCriarAnuncio);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
    }

    private void setupRecyclerViews() {
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewLocais.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecentes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopulares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMelhoresAvaliacoes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        locaisAdapter = new LocalAdapter(this, new ArrayList<>());
        recentesAdapter = new LocalAdapter(this, new ArrayList<>());
        popularesAdapter = new LocalAdapter(this, new ArrayList<>());
        melhoresAvaliacoesAdapter = new LocalAdapter(this, new ArrayList<>());

        recyclerViewLocais.setAdapter(locaisAdapter);
        recyclerViewRecentes.setAdapter(recentesAdapter);
        recyclerViewPopulares.setAdapter(popularesAdapter);
        recyclerViewMelhoresAvaliacoes.setAdapter(melhoresAvaliacoesAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_messages) {
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

    private void setupSearch() {
        editTextPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLocais(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFab() {
        fabCriarAnuncio.setOnClickListener(v -> {
            if (firebaseManager.isLoggedIn()) {
                startActivity(new Intent(this, CriarAnuncioActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    private void loadCategorias() {
        List<Categoria> categorias = getCategorias();
        categoriaAdapter = new CategoriaAdapter(this, categorias, categoria -> {
            selectedCategory = (categoria != null) ? categoria.getNome() : null;
            filtrarLocais(editTextPesquisa.getText().toString());
        });
        recyclerViewCategorias.setAdapter(categoriaAdapter);
    }

    private void filtrarLocais(String texto) {
        if (allLocais == null) return;

        List<Local> locaisFiltrados = new ArrayList<>();

        if (texto.isEmpty() && selectedCategory == null) {
            locaisFiltrados.addAll(allLocais);
            textViewEmptyState.setVisibility(View.GONE);
        } else {
            String textoLower = texto.toLowerCase();
            for (Local local : allLocais) {
                boolean matchesText = texto.isEmpty() ||
                        local.getNome().toLowerCase().contains(textoLower) ||
                        local.getEndereco().toLowerCase().contains(textoLower) ||
                        local.getCategoria().toLowerCase().contains(textoLower);

                boolean matchesCategory = selectedCategory == null ||
                        local.getCategoria().equals(selectedCategory);

                if (matchesText && matchesCategory) {
                    locaisFiltrados.add(local);
                }
            }

            if (locaisFiltrados.isEmpty()) {
                textViewEmptyState.setVisibility(View.VISIBLE);
                String mensagem = "Nenhum resultado encontrado";
                if (!texto.isEmpty()) {
                    mensagem += " para \"" + texto + "\"";
                }
                if (selectedCategory != null) {
                    mensagem += " na categoria " + selectedCategory;
                }
                textViewEmptyState.setText(mensagem);
            } else {
                textViewEmptyState.setVisibility(View.GONE);
            }
        }

        locaisAdapter.updateData(locaisFiltrados);
    }

    private void loadLocaisFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("locais")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        allLocais = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Local local = document.toObject(Local.class);
                            if (local.getId() == null) {
                                local.setId(document.getId());
                            }
                            allLocais.add(local);
                        }

                        locaisAdapter.updateData(allLocais);
                        updateSortedLists();

                        if (allLocais.isEmpty()) {
                            textViewEmptyState.setVisibility(View.VISIBLE);
                            textViewEmptyState.setText("Nenhum espaço disponível no momento");
                        } else {
                            textViewEmptyState.setVisibility(View.GONE);
                        }
                    } else {
                        textViewEmptyState.setVisibility(View.VISIBLE);
                        textViewEmptyState.setText("Erro ao carregar espaços. Verifique sua conexão.");
                    }
                });
    }

    private void updateSortedLists() {
        if (allLocais == null || allLocais.isEmpty()) return;

        List<Local> recentes = new ArrayList<>(allLocais);
        Collections.sort(recentes, (l1, l2) -> Long.compare(l2.getTimestamp(), l1.getTimestamp()));
        recentesAdapter.updateData(recentes);

        List<Local> populares = new ArrayList<>(allLocais);
        Collections.sort(populares, (l1, l2) -> Integer.compare(l2.getViewCount(), l1.getViewCount()));
        popularesAdapter.updateData(populares);

        List<Local> melhoresAvaliacoes = new ArrayList<>(allLocais);
        Collections.sort(melhoresAvaliacoes, (l1, l2) -> Double.compare(l2.getRating(), l1.getRating()));
        melhoresAvaliacoesAdapter.updateData(melhoresAvaliacoes);
    }

    private List<Categoria> getCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria("Escritório", R.drawable.ic_office));
        categorias.add(new Categoria("Sala de Reunião", R.drawable.ic_meeting_room));
        categorias.add(new Categoria("Coworking", R.drawable.ic_coworking));
        categorias.add(new Categoria("Auditório", R.drawable.ic_auditorium));
        categorias.add(new Categoria("Estúdio", R.drawable.ic_studio));
        categorias.add(new Categoria("Sala de Treinamento", R.drawable.ic_training_room));
        return categorias;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        loadLocaisFromFirebase();
    }
}
