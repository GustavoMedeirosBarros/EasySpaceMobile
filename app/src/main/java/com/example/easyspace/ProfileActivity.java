package com.example.easyspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.easyspace.models.Usuario;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageViewFotoPerfil;
    private MaterialButton buttonEditarPerfil;
    private TextView textViewIniciais, textViewNome, textViewEmail;
    private TextView textViewAnunciosCount, textViewReservasCount, textViewAvaliacoesCount;
    private LinearLayout menuMyListings, menuMyReservations, menuMyReviews;
    private LinearLayout menuFavorites, menuNotifications, menuSettings, menuHelp;
    private MaterialButton buttonLogout;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseManager = new FirebaseManager();
        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        if (firebaseManager.isLoggedIn()) {
            loadUserData();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initViews() {
        imageViewFotoPerfil = findViewById(R.id.imageViewFotoPerfil);
        buttonEditarPerfil = findViewById(R.id.buttonEditarPerfil);
        textViewIniciais = findViewById(R.id.textViewIniciais);
        textViewNome = findViewById(R.id.textViewNome);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewAnunciosCount = findViewById(R.id.textViewAnunciosCount);
        textViewReservasCount = findViewById(R.id.textViewReservasCount);
        textViewAvaliacoesCount = findViewById(R.id.textViewAvaliacoesCount);
        menuMyListings = findViewById(R.id.menuMyListings);
        menuMyReservations = findViewById(R.id.menuMyReservations);
        menuMyReviews = findViewById(R.id.menuMyReviews);
        menuFavorites = findViewById(R.id.menuFavorites);
        menuNotifications = findViewById(R.id.menuNotifications);
        menuSettings = findViewById(R.id.menuSettings);
        menuHelp = findViewById(R.id.menuHelp);
        buttonLogout = findViewById(R.id.buttonLogout);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupListeners() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        buttonEditarPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        menuMyListings.setOnClickListener(v -> {
            startActivity(new Intent(this, MeusAnunciosActivity.class));
        });

        menuMyReservations.setOnClickListener(v -> {
            startActivity(new Intent(this, MinhasReservasActivity.class));
        });

        menuMyReviews.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        menuFavorites.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
        });

        menuNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        menuSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        menuHelp.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
        });

        buttonLogout.setOnClickListener(v -> {
            firebaseManager.logout();
            Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = firebaseManager.getCurrentUserId();

        if (userId == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        firebaseManager.getUserData(userId, new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                progressBar.setVisibility(View.GONE);

                String nome = usuario.getNome();
                String email = usuario.getEmail();
                String fotoUrl = usuario.getFotoUrl();

                textViewNome.setText(nome != null ? nome : "Usuário");
                textViewEmail.setText(email != null ? email : "");

                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    textViewIniciais.setVisibility(View.GONE);
                    imageViewFotoPerfil.setVisibility(View.VISIBLE);

                    if (fotoUrl.startsWith("http")) {
                        Glide.with(ProfileActivity.this)
                                .load(fotoUrl)
                                .circleCrop()
                                .into(imageViewFotoPerfil);
                    } else {

                        try {
                            byte[] decodedString = Base64.decode(fotoUrl, Base64.DEFAULT);
                            Glide.with(ProfileActivity.this)
                                    .load(decodedString)
                                    .circleCrop()
                                    .into(imageViewFotoPerfil);
                        } catch (Exception e) {
                            showFallbackInitials(nome);
                        }
                    }
                } else {
                    showFallbackInitials(nome);
                }
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Erro ao carregar dados: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        firebaseManager.getUserListingCount(userId, new FirebaseManager.CountCallback() {
            @Override
            public void onSuccess(int count) {
                textViewAnunciosCount.setText(String.valueOf(count));
            }
            @Override
            public void onFailure(String error) {
                textViewAnunciosCount.setText("0");
            }
        });

        firebaseManager.getUserReservationCount(userId, new FirebaseManager.CountCallback() {
            @Override
            public void onSuccess(int count) {
                textViewReservasCount.setText(String.valueOf(count));
            }
            @Override
            public void onFailure(String error) {
                textViewReservasCount.setText("0");
            }
        });

        textViewAvaliacoesCount.setText("0");
    }

    private void showFallbackInitials(String nome) {
        imageViewFotoPerfil.setVisibility(View.GONE);
        textViewIniciais.setVisibility(View.VISIBLE);
        if (nome != null && !nome.isEmpty()) {
            String[] partes = nome.split(" ");
            String iniciais;
            if (partes.length > 1) {
                iniciais = String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[partes.length - 1].charAt(0));
            } else {
                iniciais = String.valueOf(nome.charAt(0));
            }
            textViewIniciais.setText(iniciais.toUpperCase());
        } else {
            textViewIniciais.setText("?");
        }
    }
}