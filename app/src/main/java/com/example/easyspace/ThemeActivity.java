package com.example.easyspace;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class ThemeActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RadioGroup radioGroupThemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        radioGroupThemes = findViewById(R.id.radioGroupThemes);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tema");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        radioGroupThemes.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                String theme = radioButton.getText().toString();
                Toast.makeText(this, "Tema selecionado: " + theme, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
