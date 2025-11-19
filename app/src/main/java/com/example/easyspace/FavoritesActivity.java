package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.LocalAdapter;
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private TextView textViewFavoritesCount;
    private LinearLayout layoutEmptyState;
    private MaterialButton buttonExplore;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;

    private LocalAdapter favoritesAdapter;
    private FirebaseManager firebaseManager;
    private List<Local> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        firebaseManager = new FirebaseManager();
        favoritesList = new ArrayList<>();

        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        setupListeners();
        loadFavorites();
    }

    private void initViews() {
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        textViewFavoritesCount = findViewById(R.id.textViewFavoritesCount);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        buttonExplore = findViewById(R.id.buttonExplore);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewFavorites.setLayoutManager(gridLayoutManager);

        favoritesAdapter = new LocalAdapter(this, favoritesList);
        recyclerViewFavorites.setAdapter(favoritesAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorites) {
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
                if (firebaseManager.isLoggedIn()) {
                    startActivity(new Intent(this, MinhasReservasActivity.class));
                }
                else {
                    Toast.makeText(this, "Você precisa estar logado para ver suas reservas.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
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

    private void setupListeners() {
        buttonExplore.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void loadFavorites() {
        if (!firebaseManager.isLoggedIn()) {
            showEmptyState();
            Toast.makeText(this, "Faça login para ver seus favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getUserFavorites(new FirebaseManager.FavoritesCallback() {
            @Override
            public void onSuccess(List<Local> favorites) {
                progressBar.setVisibility(View.GONE);
                favoritesList.clear();
                favoritesList.addAll(favorites);

                if (favoritesList.isEmpty()) {
                    showEmptyState();
                } else {
                    showFavorites();
                }

                updateFavoritesCount();
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                showEmptyState();
                Toast.makeText(FavoritesActivity.this,
                        "Erro ao carregar favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerViewFavorites.setVisibility(View.GONE);
    }

    private void showFavorites() {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewFavorites.setVisibility(View.VISIBLE);
    }

    private void updateFavoritesCount() {
        int count = favoritesList.size();
        String text = count == 1 ? "1 espaço salvo" : count + " espaços salvos";
        textViewFavoritesCount.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_favorites);
        loadFavorites();
    }
}
