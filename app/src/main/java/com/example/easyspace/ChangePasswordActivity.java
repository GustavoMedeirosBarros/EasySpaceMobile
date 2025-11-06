package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private MaterialButton buttonChangePassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Alterar Senha");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        buttonChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {
            editTextCurrentPassword.setError("Senha atual é obrigatória");
            return;
        }

        if (newPassword.isEmpty()) {
            editTextNewPassword.setError("Nova senha é obrigatória");
            return;
        }

        if (newPassword.length() < 6) {
            editTextNewPassword.setError("Senha deve ter no mínimo 6 caracteres");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("As senhas não coincidem");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonChangePassword.setEnabled(false);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid1 -> {
                                    progressBar.setVisibility(View.GONE);
                                    buttonChangePassword.setEnabled(true);
                                    Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    buttonChangePassword.setEnabled(true);
                                    Toast.makeText(this, "Erro ao alterar senha", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        buttonChangePassword.setEnabled(true);
                        Toast.makeText(this, "Senha atual incorreta", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
