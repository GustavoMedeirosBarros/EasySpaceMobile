package com.example.easyspace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.easyspace.models.Local;
import com.example.easyspace.utils.MaskTextWatcher;
import com.example.easyspace.utils.ValidationUtils;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import java.util.Locale;
import java.io.IOException;

public class CriarAnuncioActivity extends AppCompatActivity {

    private ImageButton buttonVoltar;
    private TextInputEditText editTextNome, editTextDescricao;
    private TextInputEditText editTextPreco, editTextCapacidade, editTextHorario;
    private TextInputEditText editTextCEP, editTextRua, editTextNumero, editTextComplemento;
    private TextInputEditText editTextBairro, editTextCidade, editTextEstado;
    private AutoCompleteTextView autoCompleteCategoria, autoCompleteTipoLocacao;
    private CheckBox checkWifi, checkArCondicionado, checkCafe, checkProjetor;
    private CheckBox checkEstacionamento, checkAcessibilidade;
    private MaterialButton buttonPublicar;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    private MaterialCardView cardAddFoto;
    private LinearLayout layoutFotos;

    private FirebaseManager firebaseManager;
    private String[] categorias = {"Escritório", "Sala de Reunião", "Coworking",
            "Auditório", "Estúdio", "Sala de Treinamento"};
    private String[] tiposLocacao = {"Por Hora", "Por Dia", "Por Semana", "Por Mês"};

    private List<Uri> selectedImages = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_anuncio);

        firebaseManager = new FirebaseManager();

        initViews();
        setupImagePicker();
        setupListeners();
        setupDropdowns();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.getMenu().findItem(R.id.nav_criar_anuncio).setChecked(true);
        }
    }

    private void initViews() {
        buttonVoltar = findViewById(R.id.buttonVoltar);
        editTextNome = findViewById(R.id.editTextNome);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        editTextPreco = findViewById(R.id.editTextPreco);
        editTextCapacidade = findViewById(R.id.editTextCapacidade);
        editTextHorario = findViewById(R.id.editTextHorario);
        editTextCEP = findViewById(R.id.editTextCEP);
        editTextRua = findViewById(R.id.editTextRua);
        editTextNumero = findViewById(R.id.editTextNumero);
        editTextComplemento = findViewById(R.id.editTextComplemento);
        editTextBairro = findViewById(R.id.editTextBairro);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        autoCompleteTipoLocacao = findViewById(R.id.autoCompleteTipoLocacao);
        checkWifi = findViewById(R.id.checkWifi);
        checkArCondicionado = findViewById(R.id.checkArCondicionado);
        checkCafe = findViewById(R.id.checkCafe);
        checkProjetor = findViewById(R.id.checkProjetor);
        checkEstacionamento = findViewById(R.id.checkEstacionamento);
        checkAcessibilidade = findViewById(R.id.checkAcessibilidade);
        buttonPublicar = findViewById(R.id.buttonPublicar);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        cardAddFoto = findViewById(R.id.cardAddFoto);
        layoutFotos = findViewById(R.id.layoutFotos);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count && selectedImages.size() < 10; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                selectedImages.add(imageUri);
                            }
                        } else if (data.getData() != null) {
                            selectedImages.add(data.getData());
                        }

                        updatePhotoPreview();
                    }
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permissão necessária para selecionar fotos", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        buttonVoltar.setOnClickListener(v -> finish());
        buttonPublicar.setOnClickListener(v -> publicarAnuncio());
        cardAddFoto.setOnClickListener(v -> checkPermissionAndPickImage());

        editTextCEP.addTextChangedListener(new MaskTextWatcher(editTextCEP, "#####-###"));

        editTextCEP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cep = MaskTextWatcher.unmask(s.toString());
                if (cep.length() == 8) {
                    buscarCEP(cep);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updatePhotoPreview() {
        layoutFotos.removeAllViews();

        layoutFotos.addView(cardAddFoto);

        for (int i = 0; i < selectedImages.size(); i++) {
            final int index = i;
            Uri imageUri = selectedImages.get(i);

            MaterialCardView cardView = new MaterialCardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (120 * getResources().getDisplayMetrics().density),
                    (int) (120 * getResources().getDisplayMetrics().density)
            );
            params.setMarginEnd((int) (12 * getResources().getDisplayMetrics().density));
            cardView.setLayoutParams(params);
            cardView.setRadius(8 * getResources().getDisplayMetrics().density);
            cardView.setCardElevation(2 * getResources().getDisplayMetrics().density);

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(imageUri);

            ImageButton btnRemove = new ImageButton(this);
            btnRemove.setLayoutParams(new ViewGroup.LayoutParams(
                    (int) (32 * getResources().getDisplayMetrics().density),
                    (int) (32 * getResources().getDisplayMetrics().density)
            ));
            btnRemove.setImageResource(R.drawable.ic_close);
            btnRemove.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background));
            btnRemove.setOnClickListener(v -> {
                selectedImages.remove(index);
                updatePhotoPreview();
            });

            cardView.addView(imageView);
            layoutFotos.addView(cardView);
        }
    }

    private void setupDropdowns() {
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categorias);
        autoCompleteCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<String> tipoLocacaoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, tiposLocacao);
        autoCompleteTipoLocacao.setAdapter(tipoLocacaoAdapter);
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
            } else if (itemId == R.id.nav_criar_anuncio) {
                return true;
            } else if (itemId == R.id.nav_reservations) {
                startActivity(new Intent(this, MinhasReservasActivity.class));
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

    private void buscarCEP(String cep) {
        progressBar.setVisibility(View.VISIBLE);

        ValidationUtils.buscarCEP(cep, new ValidationUtils.CepCallback() {
            @Override
            public void onSuccess(String endereco, String bairro, String cidade, String estado) {
                runOnUiThread(() -> {
                    editTextRua.setText(endereco);
                    editTextBairro.setText(bairro);
                    editTextCidade.setText(cidade);
                    editTextEstado.setText(estado);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(CriarAnuncioActivity.this, message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void publicarAnuncio() {
        if (selectedImages.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos 1 foto do espaço", Toast.LENGTH_SHORT).show();
            return;
        }

        String nome = editTextNome.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        String rua = editTextRua.getText().toString().trim();
        String numero = editTextNumero.getText().toString().trim();
        String complemento = editTextComplemento.getText().toString().trim();
        String bairro = editTextBairro.getText().toString().trim();
        String cidade = editTextCidade.getText().toString().trim();
        String estado = editTextEstado.getText().toString().trim();
        String cep = MaskTextWatcher.unmask(editTextCEP.getText().toString().trim());
        String precoStr = editTextPreco.getText().toString().trim();
        String capacidadeStr = editTextCapacidade.getText().toString().trim();
        String horario = editTextHorario.getText().toString().trim();
        String categoria = autoCompleteCategoria.getText().toString().trim();
        String tipoLocacao = autoCompleteTipoLocacao.getText().toString().trim();

        if (nome.isEmpty()) {
            editTextNome.setError("Digite o nome do espaço");
            editTextNome.requestFocus();
            return;
        }

        if (descricao.isEmpty()) {
            editTextDescricao.setError("Digite a descrição");
            editTextDescricao.requestFocus();
            return;
        }

        if (categoria.isEmpty()) {
            autoCompleteCategoria.setError("Selecione uma categoria");
            autoCompleteCategoria.requestFocus();
            return;
        }

        if (cep.isEmpty()) {
            editTextCEP.setError("Digite o CEP");
            editTextCEP.requestFocus();
            return;
        }

        if (rua.isEmpty()) {
            editTextRua.setError("Digite a rua");
            editTextRua.requestFocus();
            return;
        }

        if (numero.isEmpty()) {
            editTextNumero.setError("Digite o número");
            editTextNumero.requestFocus();
            return;
        }

        if (bairro.isEmpty()) {
            editTextBairro.setError("Digite o bairro");
            editTextBairro.requestFocus();
            return;
        }

        if (precoStr.isEmpty()) {
            editTextPreco.setError("Digite o preço");
            editTextPreco.requestFocus();
            return;
        }

        if (tipoLocacao.isEmpty()) {
            autoCompleteTipoLocacao.setError("Selecione o tipo de locação");
            autoCompleteTipoLocacao.requestFocus();
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            editTextPreco.setError("Preço inválido");
            editTextPreco.requestFocus();
            return;
        }

        int capacidade = 0;
        if (!capacidadeStr.isEmpty()) {
            try {
                capacidade = Integer.parseInt(capacidadeStr);
            } catch (NumberFormatException e) {
                editTextCapacidade.setError("Capacidade inválida");
                editTextCapacidade.requestFocus();
                return;
            }
        }

        String enderecoCompleto = rua + ", " + numero +
                (complemento.isEmpty() ? "" : " - " + complemento) +
                " - " + bairro + ", " + cidade + " - " + estado + ", " + cep;

        Local local = new Local();
        local.setNome(nome);
        local.setDescricao(descricao);
        local.setEndereco(enderecoCompleto);
        local.setPreco(preco);
        local.setCategoria(categoria);
        local.setCapacidade(capacidade);
        local.setHorarioFuncionamento(horario);
        local.setTipoLocacao(tipoLocacao);
        local.setRating(0.0);
        local.setViewCount(0);

        List<String> comodidades = new ArrayList<>();
        if (checkWifi.isChecked()) comodidades.add("Wi-Fi");
        if (checkArCondicionado.isChecked()) comodidades.add("Ar Condicionado");
        if (checkCafe.isChecked()) comodidades.add("Café");
        if (checkProjetor.isChecked()) comodidades.add("Projetor");
        if (checkEstacionamento.isChecked()) comodidades.add("Estacionamento");
        if (checkAcessibilidade.isChecked()) comodidades.add("Acessibilidade");
        local.setComodidades(comodidades);

        progressBar.setVisibility(View.VISIBLE);
        buttonPublicar.setEnabled(false);

        geocodeAddress(enderecoCompleto, (latitude, longitude) -> {
            local.setLatitude(latitude);
            local.setLongitude(longitude);

            convertImagesToBase64(selectedImages, base64Images -> {
                if (!base64Images.isEmpty()) {
                    local.setImageUrl(base64Images.get(0));
                }

                firebaseManager.salvarLocal(local, new FirebaseManager.TaskCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CriarAnuncioActivity.this,
                                    "Anúncio publicado com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CriarAnuncioActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            buttonPublicar.setEnabled(true);
                            Toast.makeText(CriarAnuncioActivity.this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            });
        });
    }

    private void geocodeAddress(String address, OnGeocodeListener listener) {
        new Thread(() -> {
            double latitude = 0;
            double longitude = 0;

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(address, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else {
                    latitude = -23.5505;
                    longitude = -46.6333;
                }
            } catch (IOException e) {
                e.printStackTrace();
                latitude = -23.5505;
                longitude = -46.6333;
            }

            double finalLat = latitude;
            double finalLon = longitude;

            runOnUiThread(() -> listener.onGeocode(finalLat, finalLon));
        }).start();
    }

    private interface OnGeocodeListener {
        void onGeocode(double latitude, double longitude);
    }

    private void convertImagesToBase64(List<Uri> imageUris, OnImagesConvertedListener listener) {
        List<String> base64Images = new ArrayList<>();

        if (imageUris.isEmpty()) {
            listener.onImagesConverted(base64Images);
            return;
        }

        try {
            Uri imageUri = imageUris.get(0);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 800);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            base64Images.add(base64Image);
            listener.onImagesConverted(base64Images);

        } catch (Exception e) {
            e.printStackTrace();
            listener.onImagesConverted(base64Images);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    private interface OnImagesConvertedListener {
        void onImagesConverted(List<String> base64Images);
    }
}