package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.utils.UserManager;
import com.example.easyspace.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private MaterialButton buttonLogin, buttonGoogleLogin;
    private TextView textViewRegister;
    private ImageView imageViewClose;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = new UserManager(this);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        imageViewClose = findViewById(R.id.imageViewClose);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> performLogin());

        buttonGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Login com Google não implementado", Toast.LENGTH_SHORT).show();
        });

        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        imageViewClose.setOnClickListener(v -> finish());
    }

    private void performLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email é obrigatório");
            editTextEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            editTextEmail.setError("Email inválido");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Senha é obrigatória");
            editTextPassword.requestFocus();
            return;
        }

        boolean success = userManager.loginUser(email, password);

        if (success) {
            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show();
        }
    }
}
