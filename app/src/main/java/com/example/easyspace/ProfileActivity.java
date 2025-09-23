package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyspace.utils.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageViewClose;
    private TextView textViewNome, textViewEmail, textViewTelefone, textViewCidade, textViewEstado;
    private Button buttonLogout;
    private TextView textViewEmailCollapsed;
    private UserManager userManager;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = new UserManager(this);
        initViews();
        loadUserData();
        setupListeners();
        setupToolbar();
    }

    private void initViews() {
        imageViewClose = findViewById(R.id.imageViewClose);
        textViewNome = findViewById(R.id.textViewNome);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewTelefone = findViewById(R.id.textViewTelefone);
        textViewCidade = findViewById(R.id.textViewCidade);
        textViewEstado = findViewById(R.id.textViewEstado);
        textViewEmailCollapsed = findViewById(R.id.textViewEmailCollapsed);
        buttonLogout = findViewById(R.id.buttonLogout);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void loadUserData() {
        JSONObject currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            try {
                textViewNome.setText(currentUser.getString("nome"));
                textViewEmail.setText(currentUser.getString("email"));
                textViewEmailCollapsed.setText(currentUser.getString("email"));
                textViewTelefone.setText(currentUser.getString("telefone"));
                textViewCidade.setText(currentUser.getString("cidade"));
                textViewEstado.setText(currentUser.getString("estado"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupListeners() {
        imageViewClose.setOnClickListener(v -> finish());

        buttonLogout.setOnClickListener(v -> {
            userManager.logout();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}