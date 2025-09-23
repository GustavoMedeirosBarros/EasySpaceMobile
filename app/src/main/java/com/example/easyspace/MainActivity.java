package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.easyspace.adapters.LocalAdapter;
import com.example.easyspace.adapters.CategoriaAdapter;
import com.example.easyspace.models.Local;
import com.example.easyspace.models.Categoria;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private RecyclerView recyclerViewCategorias, recyclerViewLocais, recyclerViewRecentes,
            recyclerViewPopulares, recyclerViewMelhoresAvaliacoes;
    private BottomNavigationView bottomNavigation;

    private CategoriaAdapter categoriaAdapter;
    private LocalAdapter locaisAdapter, recentesAdapter, popularesAdapter, melhoresAdapter;

    private UserManager userManager;
    private List<Local> allLocais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = new UserManager(this);
        initViews();
        setupRecyclerViews();
        setupBottomNavigation();
        setupSearch();
        loadData();
    }

    private void initViews() {
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recyclerViewLocais = findViewById(R.id.recyclerViewLocais);
        recyclerViewRecentes = findViewById(R.id.recyclerViewRecentes);
        recyclerViewPopulares = findViewById(R.id.recyclerViewPopulares);
        recyclerViewMelhoresAvaliacoes = findViewById(R.id.recyclerViewMelhoresAvaliacoes);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerViews() {
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriaAdapter = new CategoriaAdapter(this, new ArrayList<>());
        recyclerViewCategorias.setAdapter(categoriaAdapter);

        recyclerViewLocais.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        locaisAdapter = new LocalAdapter(this);
        recyclerViewLocais.setAdapter(locaisAdapter);

        recyclerViewRecentes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentesAdapter = new LocalAdapter(this);
        recyclerViewRecentes.setAdapter(recentesAdapter);

        recyclerViewPopulares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularesAdapter = new LocalAdapter(this);
        recyclerViewPopulares.setAdapter(popularesAdapter);

        recyclerViewMelhoresAvaliacoes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        melhoresAdapter = new LocalAdapter(this);
        recyclerViewMelhoresAvaliacoes.setAdapter(melhoresAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                // Falta implementar busca
                return true;
            } else if (itemId == R.id.nav_favorites) {
                // Falta implementar favoritos
                return true;
            } else if (itemId == R.id.nav_messages) {
                // Falta implementar mensagens
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (userManager.isLoggedIn()) {
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
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLocais(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterLocais(String query) {
        if (allLocais == null) return;

        List<Local> filteredList = new ArrayList<>();
        for (Local local : allLocais) {
            if (local.getNome().toLowerCase().contains(query.toLowerCase()) ||
                    local.getDescricao().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(local);
            }
        }

        locaisAdapter.updateData(filteredList);
    }

    private void loadData() {
        List<Categoria> categorias = getCategorias();
        categoriaAdapter.updateData(categorias);

        allLocais = getLocais();

        List<Local> destaque = allLocais.subList(0, Math.min(5, allLocais.size()));
        List<Local> recentes = allLocais.subList(0, Math.min(4, allLocais.size()));
        List<Local> populares = allLocais.subList(0, Math.min(6, allLocais.size()));
        List<Local> melhores = allLocais.subList(0, Math.min(3, allLocais.size()));

        locaisAdapter.updateData(destaque);
        recentesAdapter.updateData(recentes);
        popularesAdapter.updateData(populares);
        melhoresAdapter.updateData(melhores);
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

    private List<Local> getLocais() {
        List<Local> locais = new ArrayList<>();

        locais.add(new Local("Espaço Inovação", "Escritório moderno no centro", 150.0, "Centro", 4.8f,
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=400"));
        locais.add(new Local("CoWork Plus", "Ambiente colaborativo", 80.0, "Vila Olímpia", 4.5f,
                "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=400"));
        locais.add(new Local("Sala Executive", "Sala de reunião premium", 200.0, "Faria Lima", 4.9f,
                "https://images.unsplash.com/photo-1431540015161-0bf868a2d407?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"));
        locais.add(new Local("Studio Creative", "Estúdio para gravações", 120.0, "Pinheiros", 4.6f,
                "https://images.unsplash.com/photo-1563089145-599997674d42?w=400"));
        locais.add(new Local("Auditório Central", "Espaço para eventos", 300.0, "Centro", 4.7f,
                "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400"));
        locais.add(new Local("Sala de Treinamento", "Ideal para cursos", 100.0, "Brooklin", 4.4f,
                "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=400"));

        return locais;
    }
}
