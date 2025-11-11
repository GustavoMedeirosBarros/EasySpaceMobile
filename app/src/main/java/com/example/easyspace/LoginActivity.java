package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.utils.FirebaseManager;
import com.example.easyspace.utils.ValidationUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextSenha;
    private MaterialButton buttonLogin, buttonGoogleLogin;
    private TextView textViewRegister;
    private ImageView imageViewClose;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    private FirebaseManager firebaseManager;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseManager = new FirebaseManager();
        firebaseManager.configureGoogleSignIn(this);

        initializeGoogleSignInLauncher();

        initViews();
        setupListeners();
        setupBottomNavigation();
    }

    private void initializeGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            handleGoogleSignInResult(task);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            buttonGoogleLogin.setEnabled(true);
                            Toast.makeText(this, "Erro ao obter dados do Google", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonGoogleLogin.setEnabled(true);
                        Toast.makeText(this, "Login com Google cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        imageViewClose = findViewById(R.id.imageViewClose);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        imageViewClose.setOnClickListener(v -> finish());

        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        buttonGoogleLogin.setOnClickListener(v -> iniciarLoginGoogle());

        buttonLogin.setOnClickListener(v -> fazerLogin());
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
                if (firebaseManager.isLoggedIn()) {
                    startActivity(new Intent(this, CriarAnuncioActivity.class));
                } else {
                    Toast.makeText(this, "Você precisa estar logado para criar um anúncio.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
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

    private void iniciarLoginGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        buttonGoogleLogin.setEnabled(false);
        buttonLogin.setEnabled(false);

        Intent signInIntent = firebaseManager.getGoogleSignInClient().getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            firebaseManager.signInWithGoogle(task, new FirebaseManager.AuthCallback() {
                @Override
                public void onSuccess(String userId) {
                    firebaseManager.isProfileComplete(userId, isComplete -> {
                        progressBar.setVisibility(View.GONE);

                        updateFcmTokenAndRedirect(isComplete);
                    });
                }

                @Override
                public void onFailure(String error) {
                    progressBar.setVisibility(View.GONE);
                    buttonGoogleLogin.setEnabled(true);
                    buttonLogin.setEnabled(true);

                    String errorMessage;
                    if (error.contains("network") || error.contains("NETWORK_ERROR") || error.contains("Connectivity")) {
                        errorMessage = "Erro de conexão. Verifique sua internet e tente novamente.";
                    } else if (error.contains("SIGN_IN_CANCELLED") || error.contains("12501")) {
                        errorMessage = "Login cancelado pelo usuário";
                    } else if (error.contains("SIGN_IN_FAILED") || error.contains("12500")) {
                        errorMessage = "Falha no login. Verifique suas configurações do Google Play Services";
                    } else if (error.contains("DEVELOPER_ERROR") || error.contains("10")) {
                        errorMessage = "Erro de configuração. Entre em contato com o suporte";
                    } else {
                        errorMessage = "Erro ao fazer login com Google. Tente novamente mais tarde.";
                    }

                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            buttonGoogleLogin.setEnabled(true);
            buttonLogin.setEnabled(true);

            String errorMsg = "Erro ao processar login com Google";
            if (e.getMessage() != null && e.getMessage().contains("network")) {
                errorMsg = "Erro de conexão. Verifique sua internet";
            }

            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void fazerLogin() {
        String email = ValidationUtils.sanitizeInput(editTextEmail.getText().toString().trim().toLowerCase());
        String senha = editTextSenha.getText().toString();

        if (email.isEmpty()) {
            editTextEmail.setError("Email é obrigatório");
            editTextEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            editTextEmail.setError("Email inválido");
            editTextEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            editTextSenha.setError("Senha é obrigatória");
            editTextSenha.requestFocus();
            return;
        }

        if (senha.length() < 6) {
            editTextSenha.setError("Senha deve ter pelo menos 6 caracteres");
            editTextSenha.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);
        buttonGoogleLogin.setEnabled(false);

        firebaseManager.loginUser(email, senha, new FirebaseManager.AuthCallback() {
            public void onSuccess(String userId) {
                progressBar.setVisibility(View.GONE);

                updateFcmTokenAndRedirect(true);
            }

            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
                buttonGoogleLogin.setEnabled(true);

                String errorMessage = "Email ou senha incorretos";
                if (error.contains("network")) {
                    errorMessage = "Erro de conexão. Verifique sua internet";
                }

                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Busca o token FCM mais recente e o salva no Firestore
     * antes de redirecionar o usuário.
     */
    private void updateFcmTokenAndRedirect(boolean isProfileComplete) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult();
                Log.d("LoginActivity", "FCM Token: " + token);
                firebaseManager.updateFcmToken(token);
            } else {
                Log.w("LoginActivity", "Fetching FCM registration token failed", task.getException());
            }

            if (isProfileComplete) {
                Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finishAffinity();
            } else {
                Toast.makeText(LoginActivity.this, "Complete seu perfil para continuar", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, CompleteProfileActivity.class));
                finishAffinity();
            }
        });
    }
}