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
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.easyspace.models.Usuario;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ImageView imageViewEditFoto;
    private TextView textViewIniciaisEdit, textViewAlterarFoto;
    private TextInputEditText editTextNome, editTextEmail, editTextTelefone;
    private TextInputEditText editTextCEP, editTextEndereco, editTextBairro, editTextCidade, editTextEstado;
    private MaterialButton buttonSalvar;
    private ProgressBar progressBar;

    private FirebaseManager firebaseManager;
    private Usuario currentUser;
    private Uri imageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseManager = new FirebaseManager();
        initViews();
        setupToolbar();
        setupImagePicker();
        setupListeners();
        loadUserProfile();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imageViewEditFoto = findViewById(R.id.imageViewEditFoto);
        textViewIniciaisEdit = findViewById(R.id.textViewIniciaisEdit);
        textViewAlterarFoto = findViewById(R.id.textViewAlterarFoto);
        editTextNome = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTelefone = findViewById(R.id.editTextTelefone);
        editTextCEP = findViewById(R.id.editTextCEP);
        editTextEndereco = findViewById(R.id.editTextEndereco);
        editTextBairro = findViewById(R.id.editTextBairro);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editar Perfil");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if (imageUri != null) {
                            textViewIniciaisEdit.setVisibility(View.GONE);
                            imageViewEditFoto.setVisibility(View.VISIBLE);
                            Glide.with(this).load(imageUri).circleCrop().into(imageViewEditFoto);
                        }
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
        textViewAlterarFoto.setOnClickListener(v -> checkPermissionAndPickImage());
        buttonSalvar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        firebaseManager.getUserData(userId, new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                currentUser = usuario;
                populateUserData();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUserData() {
        if (currentUser == null) return;

        editTextNome.setText(currentUser.getNome());
        editTextEmail.setText(currentUser.getEmail());
        editTextTelefone.setText(currentUser.getTelefone());
        editTextCEP.setText(currentUser.getCep());
        editTextEndereco.setText(currentUser.getEndereco());
        editTextBairro.setText(currentUser.getBairro());
        editTextCidade.setText(currentUser.getCidade());
        editTextEstado.setText(currentUser.getEstado());

        String fotoUrl = currentUser.getFotoUrl();
        String nome = currentUser.getNome();

        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            textViewIniciaisEdit.setVisibility(View.GONE);
            imageViewEditFoto.setVisibility(View.VISIBLE);

            if (fotoUrl.startsWith("http")) {
                Glide.with(this).load(fotoUrl).circleCrop().into(imageViewEditFoto);
            } else {
                try {
                    byte[] decodedString = Base64.decode(fotoUrl, Base64.DEFAULT);
                    Glide.with(this)
                            .load(decodedString)
                            .circleCrop()
                            .into(imageViewEditFoto);
                } catch (Exception e) {
                    showFallbackInitials(nome);
                }
            }
        } else {
            showFallbackInitials(nome);
        }
    }

    private void showFallbackInitials(String nome) {
        imageViewEditFoto.setVisibility(View.GONE);
        textViewIniciaisEdit.setVisibility(View.VISIBLE);
        if (nome != null && !nome.isEmpty()) {
            String[] partes = nome.split(" ");
            String iniciais;
            if (partes.length > 1) {
                iniciais = String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[partes.length - 1].charAt(0));
            } else {
                iniciais = String.valueOf(nome.charAt(0));
            }
            textViewIniciaisEdit.setText(iniciais.toUpperCase());
        } else {
            textViewIniciaisEdit.setText("?");
        }
    }

    private void salvarAlteracoes() {
        progressBar.setVisibility(View.VISIBLE);
        buttonSalvar.setEnabled(false);
        String userId = firebaseManager.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            buttonSalvar.setEnabled(true);
            return;
        }

        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = resizeBitmap(bitmap, 400, 400);
                String base64Image = bitmapToBase64(resizedBitmap);
                currentUser.setFotoUrl(base64Image);
                updateTextData();
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                buttonSalvar.setEnabled(true);
            }
        } else {
            updateTextData();
        }
    }

    private void updateTextData() {
        String nome = editTextNome.getText().toString().trim();
        String telefone = editTextTelefone.getText().toString().trim();
        String cep = editTextCEP.getText().toString().trim();
        String endereco = editTextEndereco.getText().toString().trim();
        String bairro = editTextBairro.getText().toString().trim();
        String cidade = editTextCidade.getText().toString().trim();
        String estado = editTextEstado.getText().toString().trim();

        currentUser.setNome(nome);
        currentUser.setTelefone(telefone);
        currentUser.setCep(cep);
        currentUser.setEndereco(endereco);
        currentUser.setBairro(bairro);
        currentUser.setCidade(cidade);
        currentUser.setEstado(estado);

        firebaseManager.updateUserProfile(currentUser.getId(), currentUser, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                progressBar.setVisibility(View.GONE);
                buttonSalvar.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                buttonSalvar.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Erro ao salvar: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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
}