package com.example.easyspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.easyspace.models.Local;
import com.example.easyspace.models.Usuario;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class LocalDetailActivity extends AppCompatActivity {

    private ImageView imageViewLocal;
    private TextView textViewNome, textViewCategoria, textViewEndereco;
    private TextView textViewPreco, textViewRating, textViewDescricao;
    private TextView textViewCapacidade, textViewHorario, textViewComodidades;
    private TextView textViewProprietarioNome, textViewProprietarioEmail, textViewProprietarioTelefone;
    private TextView textViewPrecoInferior;
    private MaterialButton buttonReservar;
    private ImageButton buttonVoltar, buttonFavorito;
    private MapView mapView;
    private FirebaseFirestore db;
    private Local local;
    private FirebaseManager firebaseManager;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Configuration.getInstance().setOsmdroidTileCache(getCacheDir());

        setContentView(R.layout.activity_local_detail);
        db = FirebaseFirestore.getInstance();
        firebaseManager = new FirebaseManager();
        initViews();
        loadLocalData();
        setupListeners();
        setupMap();
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
        textViewPrecoInferior = findViewById(R.id.textViewPrecoInferior);
        buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonFavorito = findViewById(R.id.buttonFavorito);
        mapView = findViewById(R.id.mapView);
    }

    private void loadLocalData() {
        local = (Local) getIntent().getSerializableExtra("local");

        if (local == null) {
            String localId = getIntent().getStringExtra("localId");
            if (localId != null) {
                firebaseManager.getLocalById(localId, new FirebaseManager.LocalCallback() {
                    @Override
                    public void onSuccess(Local localFromDb) {
                        local = localFromDb;
                        populateViews();
                        loadFavoriteStatus();
                        loadProprietarioInfo();
                    }
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LocalDetailActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                Toast.makeText(this, "Erro ao carregar dados do local", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            populateViews();
            loadFavoriteStatus();
            loadProprietarioInfo();
        }
    }

    private void populateViews() {
        if (local == null) return;

        incrementViewCount();

        textViewNome.setText(local.getNome());
        textViewCategoria.setText(local.getCategoria());
        textViewEndereco.setText(local.getEndereco());
        textViewPreco.setText(local.getPrecoFormatado());
        textViewPrecoInferior.setText(local.getPrecoFormatado());
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

        String imageUrl = local.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
                Glide.with(this).load(imageUrl).into(imageViewLocal);
            } else {
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(this).load(decodedString).into(imageViewLocal);
                } catch (Exception e) {
                    Glide.with(this).load(R.drawable.ic_default_space).into(imageViewLocal);
                }
            }
        } else {
            Glide.with(this).load(R.drawable.ic_default_space).into(imageViewLocal);
        }

        updateMapLocation();
    }

    private void incrementViewCount() {
        if (local == null || local.getId() == null) return;

        db.collection("locais").document(local.getId())
                .update("viewCount", FieldValue.increment(1));
    }

    private void loadFavoriteStatus() {
        if (!firebaseManager.isLoggedIn() || local == null || local.getId() == null) {

            buttonFavorito.setImageResource(R.drawable.ic_favorite_border);
            buttonFavorito.setColorFilter(ContextCompat.getColor(this, R.color.red));
            return;
        }

        firebaseManager.isFavorite(local.getId(), isFav -> {
            isFavorite = isFav;
            updateFavoriteButton();
        });
    }

    private void updateFavoriteButton() {
        buttonFavorito.setImageResource(isFavorite ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        buttonFavorito.setColorFilter(ContextCompat.getColor(this, R.color.red));
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
            if (local == null || local.getProprietarioId() == null) {
                Toast.makeText(this, "Erro ao identificar proprietário", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!firebaseManager.isLoggedIn()) {
                Toast.makeText(this, "Faça login para reservar", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            if (local.getProprietarioId() == null) {
                Toast.makeText(this, "Erro ao identificar proprietário", Toast.LENGTH_SHORT).show();
                return;
            }

            String proprietarioId = local.getProprietarioId();
            String meuId = firebaseManager.getCurrentUserId();

            if (proprietarioId.equals(meuId)) {
                Toast.makeText(this, "Você não pode reservar seu próprio espaço", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ReservationActivity.class);
            intent.putExtra("local", local);
            startActivity(intent);
        });
    }

    private void simularEnvioNotificacao(String proprietarioId) {
        String nomeUsuario = "Um usuário";
        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            nomeUsuario = user.getDisplayName();
        }

        String title = "Nova Reserva!";
        String message = nomeUsuario + " fez uma reserva para o seu espaço: " + local.getNome();

        buttonReservar.setEnabled(false);

        firebaseManager.sendInAppNotification(proprietarioId, title, message, new FirebaseManager.TaskCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(LocalDetailActivity.this, "Reserva solicitada! O proprietário será notificado.", Toast.LENGTH_LONG).show();
                buttonReservar.setText("Reserva Solicitada");
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LocalDetailActivity.this, "Reserva feita, mas falhou ao notificar proprietário.", Toast.LENGTH_SHORT).show();
                buttonReservar.setEnabled(true);
            }
        });
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);
    }

    private void updateMapLocation() {
        if (local == null) return;

        double lat = (local.getLatitude() != 0) ? local.getLatitude() : -23.5505;
        double lon = (local.getLongitude() != 0) ? local.getLongitude() : -46.6333;

        GeoPoint startPoint = new GeoPoint(lat, lon);
        mapView.getController().setCenter(startPoint);

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle(local.getNome());
        startMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location));

        mapView.getOverlays().clear();
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
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
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }
}