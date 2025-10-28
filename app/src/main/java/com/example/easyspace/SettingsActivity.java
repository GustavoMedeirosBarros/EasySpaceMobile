package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.easyspace.utils.SampleDataGenerator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private LinearLayout menuEditProfile, menuChangePassword, menuPrivacy;
    private LinearLayout menuLanguage, menuTheme, menuAbout;
    private LinearLayout developerSection;
    private MaterialButton buttonGenerateSampleData;
    private ProgressBar progressBarGenerate;
    private SwitchCompat switchNotifications, switchEmailNotifications;
    private SwitchCompat switchPushNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        menuEditProfile = findViewById(R.id.menuEditProfile);
        menuChangePassword = findViewById(R.id.menuChangePassword);
        menuPrivacy = findViewById(R.id.menuPrivacy);
        menuLanguage = findViewById(R.id.menuLanguage);
        menuTheme = findViewById(R.id.menuTheme);
        menuAbout = findViewById(R.id.menuAbout);
        developerSection = findViewById(R.id.developerSection);
        buttonGenerateSampleData = findViewById(R.id.buttonGenerateSampleData);
        progressBarGenerate = findViewById(R.id.progressBarGenerate);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchEmailNotifications = findViewById(R.id.switchEmailNotifications);
        switchPushNotifications = findViewById(R.id.switchPushNotifications);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configurações");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        menuEditProfile.setOnClickListener(v ->
                Toast.makeText(this, "Editar perfil", Toast.LENGTH_SHORT).show());

        menuChangePassword.setOnClickListener(v ->
                Toast.makeText(this, "Alterar senha", Toast.LENGTH_SHORT).show());

        menuPrivacy.setOnClickListener(v ->
                Toast.makeText(this, "Configurações de privacidade", Toast.LENGTH_SHORT).show());

        menuLanguage.setOnClickListener(v ->
                Toast.makeText(this, "Selecionar idioma", Toast.LENGTH_SHORT).show());

        menuTheme.setOnClickListener(v ->
                Toast.makeText(this, "Alterar tema", Toast.LENGTH_SHORT).show());

        menuAbout.setOnClickListener(v ->
                Toast.makeText(this, "Sobre o EasySpace v1.0", Toast.LENGTH_SHORT).show());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Notificações " + (isChecked ? "ativadas" : "desativadas"),
                        Toast.LENGTH_SHORT).show());

        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Notificações por email " + (isChecked ? "ativadas" : "desativadas"),
                        Toast.LENGTH_SHORT).show());

        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Notificações push " + (isChecked ? "ativadas" : "desativadas"),
                        Toast.LENGTH_SHORT).show());

        buttonGenerateSampleData.setOnClickListener(v -> showGenerateDataConfirmation());
    }

    private void showGenerateDataConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Gerar Dados de Exemplo")
                .setMessage("Isso criará múltiplos locais de exemplo no banco de dados. Esta é uma funcionalidade temporária para desenvolvimento. Deseja continuar?")
                .setPositiveButton("Sim, Gerar", (dialog, which) -> generateSampleData())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void generateSampleData() {
        buttonGenerateSampleData.setEnabled(false);
        progressBarGenerate.setVisibility(View.VISIBLE);

        SampleDataGenerator.generateAndSaveSampleData(new SampleDataGenerator.GenerateCallback() {
            @Override
            public void onSuccess(int count) {
                runOnUiThread(() -> {
                    progressBarGenerate.setVisibility(View.GONE);
                    buttonGenerateSampleData.setEnabled(true);
                    Toast.makeText(SettingsActivity.this,
                            count + " locais de exemplo criados com sucesso!",
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBarGenerate.setVisibility(View.GONE);
                    buttonGenerateSampleData.setEnabled(true);
                    Toast.makeText(SettingsActivity.this,
                            "Erro ao gerar dados: " + message,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
