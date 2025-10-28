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
        loadUserData();
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

        buttonEditarFoto.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        menuPersonalInfo.setOnClickListener(v -> {
            Toast.makeText(this, "Editar informações pessoais", Toast.LENGTH_SHORT).show();
        });

        menuLocation.setOnClickListener(v -> {
            Toast.makeText(this, "Editar localização", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Central de ajuda", Toast.LENGTH_SHORT).show();
        });

        buttonLogout.setOnClickListener(v -> {
            firebaseManager.logout();
            Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getCurrentUserData(new FirebaseManager.UserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                progressBar.setVisibility(View.GONE);

                String nome = (String) userData.get("nome");
                String email = (String) userData.get("email");
                String telefone = (String) userData.get("telefone");
                String cidade = (String) userData.get("cidade");
                String estado = (String) userData.get("estado");

                textViewNome.setText(nome != null ? nome : "Usuário");
                textViewEmail.setText(email != null ? email : "");
                textViewTelefone.setText(telefone != null ? telefone : "Não informado");

                if (cidade != null && estado != null) {
                    textViewEndereco.setText(cidade + ", " + estado);
                } else {
                    textViewEndereco.setText("Não informado");
                }

                if (nome != null && !nome.isEmpty()) {
                    String[] partes = nome.split(" ");
                    String iniciais;
                    if (partes.length > 1) {
                        iniciais = String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0));
                    } else {
                        iniciais = String.valueOf(nome.charAt(0));
                    }
                    textViewIniciais.setText(iniciais.toUpperCase());
                }
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Erro ao carregar dados: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
