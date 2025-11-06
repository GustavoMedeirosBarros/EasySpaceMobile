package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Importar o Glide
import com.example.easyspace.models.Usuario; // Importar o modelo Usuario
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageViewFotoPerfil;
    private ImageButton buttonEditarFoto;
    private TextView textViewIniciais, textViewNome, textViewEmail;
    private TextView textViewTelefone, textViewEndereco;
    private TextView textViewAnunciosCount, textViewReservasCount, textViewAvaliacoesCount;
    private LinearLayout menuPersonalInfo, menuLocation, menuMyListings;
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
        // O loadUserData() é chamado no onResume() para atualizar ao voltar
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        if (firebaseManager.isLoggedIn()) {
            loadUserData();
        } else {
            // Se não estiver logado por algum motivo, vai para o Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initViews() {
        imageViewFotoPerfil = findViewById(R.id.imageViewFotoPerfil);
        buttonEditarFoto = findViewById(R.id.buttonEditarFoto);
        textViewIniciais = findViewById(R.id.textViewIniciais);
        textViewNome = findViewById(R.id.textViewNome);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewTelefone = findViewById(R.id.textViewTelefone);
        textViewEndereco = findViewById(R.id.textViewEndereco);
        textViewAnunciosCount = findViewById(R.id.textViewAnunciosCount);
        textViewReservasCount = findViewById(R.id.textViewReservasCount);
        textViewAvaliacoesCount = findViewById(R.id.textViewAvaliacoesCount);
        menuPersonalInfo = findViewById(R.id.menuPersonalInfo);
        menuLocation = findViewById(R.id.menuLocation);
        menuMyListings = findViewById(R.id.menuMyListings);
        menuFavorites = findViewById(R.id.menuFavorites);
        menuNotifications = findViewById(R.id.menuNotifications);
        menuSettings = findViewById(R.id.menuSettings);
        menuHelp = findViewById(R.id.menuHelp);
        buttonLogout = findViewById(R.id.buttonLogout);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupListeners() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        buttonEditarFoto.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        menuPersonalInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        menuLocation.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        menuMyListings.setOnClickListener(v -> {
            startActivity(new Intent(this, MeusAnunciosActivity.class));
        });

        menuFavorites.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
        });

        textViewReservasCount.setOnClickListener(v -> {
            startActivity(new Intent(this, MinhasReservasActivity.class));
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
            finishAffinity(); // Limpa todas as activities
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = firebaseManager.getCurrentUserId();

        if (userId == null) {
            progressBar.setVisibility(View.GONE);
            // Isso não deve acontecer se a verificação no onResume() estiver correta
            return;
        }

        // 1. Carregar dados básicos do usuário
        firebaseManager.getUserData(userId, new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                progressBar.setVisibility(View.GONE);

                String nome = usuario.getNome();
                String email = usuario.getEmail();
                String telefone = usuario.getTelefone();
                String cidade = usuario.getCidade();
                String estado = usuario.getEstado();
                String fotoUrl = usuario.getFotoUrl();

                textViewNome.setText(nome != null ? nome : "Usuário");
                textViewEmail.setText(email != null ? email : "");
                textViewTelefone.setText(telefone != null && !telefone.isEmpty() ? telefone : "Não informado");

                if (cidade != null && !cidade.isEmpty() && estado != null && !estado.isEmpty()) {
                    textViewEndereco.setText(cidade + ", " + estado);
                } else {
                    textViewEndereco.setText("Não informado");
                }

                // Lógica para Iniciais ou Foto de Perfil
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    textViewIniciais.setVisibility(View.GONE);
                    imageViewFotoPerfil.setVisibility(View.VISIBLE);
                    Glide.with(ProfileActivity.this).load(fotoUrl).circleCrop().into(imageViewFotoPerfil);
                } else {
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

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Erro ao carregar dados: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Carregar contagem de anúncios
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

        // 3. Carregar contagem de reservas
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

        // 4. Contagem de avaliações (ainda não implementada no backend)
        textViewAvaliacoesCount.setText("0");
    }
}