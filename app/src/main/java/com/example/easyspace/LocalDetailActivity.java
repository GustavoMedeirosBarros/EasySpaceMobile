package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.models.Local;
import com.example.easyspace.models.Usuario;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocalDetailActivity extends AppCompatActivity {

    private ImageView imageViewLocal;
    private TextView textViewNome, textViewCategoria, textViewEndereco;
    private TextView textViewPreco, textViewRating, textViewDescricao;
    private TextView textViewCapacidade, textViewHorario, textViewComodidades;
    private TextView textViewProprietarioNome, textViewProprietarioEmail, textViewProprietarioTelefone;
    private MaterialButton buttonReservar;
    private ImageButton buttonVoltar, buttonFavorito;
    private BottomNavigationView bottomNavigation;
    private MapView mapView;
    private FirebaseFirestore db;
    private Local local;
    private FirebaseManager firebaseManager;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_detail);
        db = FirebaseFirestore.getInstance();
        firebaseManager = new FirebaseManager();
        initViews();
        loadLocalData();
        setupListeners();
        setupBottomNavigation();
        setupMap(savedInstanceState);
        loadFavoriteStatus();
        loadProprietarioInfo();
    }

    private void initViews() {
        imageViewLocal = findViewById(R.id.imageViewLocal);
        textViewNome = findViewById(R.id.textViewNome);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        textViewEndereco = findViewById(R.id.textViewEndereco);
        textViewPreco = findViewById(R.id.textViewPreco);
        textViewRating = findViewById(R.id.textViewRating);
        textViewDescricao = findViewById(R.id.textViewDescricao);
        textViewCapacidade = findViewById(R.id.textViewCapacidade);
        textViewHorario = findViewById(R.id.textViewHorario);
        textViewComodidades = findViewById(R.id.textViewComodidades);
        textViewProprietarioNome = findViewById(R.id.textViewProprietarioNome);
        textViewProprietarioEmail = findViewById(R.id.textViewProprietarioEmail);
        textViewProprietarioTelefone = findViewById(R.id.textViewProprietarioTelefone);
        buttonReservar = findViewById(R.id.buttonReservar);
        buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonFavorito = findViewById(R.id.buttonFavorito);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        mapView = findViewById(R.id.mapView);
    }

    private void loadLocalData() {
        local = (Local) getIntent().getSerializableExtra("local");

        if (local == null) {
            Toast.makeText(this, "Erro ao carregar dados do local", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        incrementViewCount();

        textViewNome.setText(local.getNome());
        textViewCategoria.setText(local.getCategoria());
        textViewEndereco.setText(local.getEndereco());
        textViewPreco.setText(local.getPrecoFormatado());
        textViewRating.setText(local.getRatingFormatado());

        if (local.getDescricao() != null && !local.getDescricao().isEmpty()) {
            textViewDescricao.setText(local.getDescricao());
        } else {
            textViewDescricao.setText("Sem descrição disponível");
        }

        if (local.getCapacidade() > 0) {
            textViewCapacidade.setText(local.getCapacidade() + " pessoas");
        } else {
            textViewCapacidade.setText("Não informado");
        }

        if (local.getHorarioFuncionamento() != null && !local.getHorarioFuncionamento().isEmpty()) {
            textViewHorario.setText(local.getHorarioFuncionamento());
        } else {
            textViewHorario.setText("Não informado");
        }

        if (local.getComodidades() != null && !local.getComodidades().isEmpty()) {
            textViewComodidades.setText(String.join(", ", local.getComodidades()));
        } else {
            textViewComodidades.setText("Nenhuma comodidade informada");
        }

        if (local.getImageUrl() != null && !local.getImageUrl().isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(local.getImageUrl(), android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageViewLocal.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                imageViewLocal.setImageResource(R.drawable.ic_default_space);
            }
        } else {
            imageViewLocal.setImageResource(R.drawable.ic_default_space);
        }
    }

    private void incrementViewCount() {
        if (local == null || local.getId() == null) return;

        db.collection("locais").document(local.getId())
                .update("viewCount", local.getViewCount() + 1)
                .addOnSuccessListener(aVoid -> {
                    local.setViewCount(local.getViewCount() + 1);
                });
    }

    private void loadFavoriteStatus() {
        if (!firebaseManager.isLoggedIn() || local == null || local.getId() == null) {
            return;
        }

        firebaseManager.isFavorite(local.getId(), isFav -> {
            isFavorite = isFav;
            updateFavoriteButton();
        });
    }

    private void updateFavoriteButton() {
        buttonFavorito.setImageResource(isFavorite ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite);
    }

    private void setupListeners() {
        buttonVoltar.setOnClickListener(v -> finish());

        buttonFavorito.setOnClickListener(v -> {
            if (!firebaseManager.isLoggedIn()) {
                Toast.makeText(this, "Faça login para adicionar favoritos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            if (local == null || local.getId() == null) {
                Toast.makeText(this, "Erro: ID do local não encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isFavorite) {
                firebaseManager.removeFromFavorites(local.getId(), new FirebaseManager.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        isFavorite = false;
                        updateFavoriteButton();
                        Toast.makeText(LocalDetailActivity.this, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LocalDetailActivity.this, "Erro ao remover favorito", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                firebaseManager.addToFavorites(local.getId(), new FirebaseManager.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        isFavorite = true;
                        updateFavoriteButton();
                        Toast.makeText(LocalDetailActivity.this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LocalDetailActivity.this, "Erro ao adicionar favorito", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        buttonReservar.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade de reserva em desenvolvimento", Toast.LENGTH_SHORT).show();
        });
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

    private void setupMap(Bundle savedInstanceState) {
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(googleMap -> {
                LatLng location;
                if (local != null && local.getLatitude() != 0 && local.getLongitude() != 0) {
                    location = new LatLng(local.getLatitude(), local.getLongitude());
                } else {
                    location = new LatLng(-23.5505, -46.6333);
                }

                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(local != null ? local.getNome() : "Local"));

                googleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(location, 15));

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
            });
        }
    }

    private void loadProprietarioInfo() {
        if (local == null || local.getProprietarioId() == null) {
            textViewProprietarioNome.setText("Informação não disponível");
            return;
        }

        firebaseManager.getUserData(local.getProprietarioId(), new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> {
                    textViewProprietarioNome.setText(usuario.getNome());
                    textViewProprietarioEmail.setText(usuario.getEmail());
                    textViewProprietarioTelefone.setText(usuario.getTelefoneFormatado());
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    textViewProprietarioNome.setText("Informação não disponível");
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
