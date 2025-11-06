package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText editTextNome, editTextTelefone;
    private TextInputEditText editTextCidade, editTextEstado;
    private MaterialButton buttonSave;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseManager = new FirebaseManager();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupToolbar();
        setupListeners();
        loadUserData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        editTextNome = findViewById(R.id.editTextNome);
        editTextTelefone = findViewById(R.id.editTextTelefone);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
        buttonSave = findViewById(R.id.buttonSave);
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

    private void setupListeners() {
        buttonSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getCurrentUserData(new FirebaseManager.UserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                progressBar.setVisibility(View.GONE);

                String nome = (String) userData.get("nome");
                String telefone = (String) userData.get("telefone");
                String cidade = (String) userData.get("cidade");
                String estado = (String) userData.get("estado");

                editTextNome.setText(nome);
                editTextTelefone.setText(telefone);
                editTextCidade.setText(cidade);
                editTextEstado.setText(estado);
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String nome = editTextNome.getText().toString().trim();
        String telefone = editTextTelefone.getText().toString().trim();
        String cidade = editTextCidade.getText().toString().trim();
        String estado = editTextEstado.getText().toString().trim();

        if (nome.isEmpty()) {
            editTextNome.setError("Nome é obrigatório");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);

        String userId = firebaseManager.getCurrentUserId();
        Map<String, Object> updates = new HashMap<>();
        updates.put("nome", nome);
        updates.put("telefone", telefone);
        updates.put("cidade", cidade);
        updates.put("estado", estado);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSave.setEnabled(true);
                    Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSave.setEnabled(true);
                    Toast.makeText(this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show();
                });
    }
}
