package com.example.easyspace;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class PrivacySettingsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private SwitchCompat switchProfileVisibility, switchShowEmail, switchShowPhone;
    private SwitchCompat switchAllowMessages, switchShowLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        switchProfileVisibility = findViewById(R.id.switchProfileVisibility);
        switchShowEmail = findViewById(R.id.switchShowEmail);
        switchShowPhone = findViewById(R.id.switchShowPhone);
        switchAllowMessages = findViewById(R.id.switchAllowMessages);
        switchShowLocation = findViewById(R.id.switchShowLocation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Privacidade");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        switchProfileVisibility.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Perfil " + (isChecked ? "público" : "privado"), Toast.LENGTH_SHORT).show());

        switchShowEmail.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Email " + (isChecked ? "visível" : "oculto"), Toast.LENGTH_SHORT).show());

        switchShowPhone.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Telefone " + (isChecked ? "visível" : "oculto"), Toast.LENGTH_SHORT).show());

        switchAllowMessages.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Mensagens " + (isChecked ? "permitidas" : "bloqueadas"), Toast.LENGTH_SHORT).show());

        switchShowLocation.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Localização " + (isChecked ? "visível" : "oculta"), Toast.LENGTH_SHORT).show());
    }
}
