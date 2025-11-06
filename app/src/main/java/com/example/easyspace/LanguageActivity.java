package com.example.easyspace;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class LanguageActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RadioGroup radioGroupLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        radioGroupLanguages = findViewById(R.id.radioGroupLanguages);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Idioma");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        radioGroupLanguages.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                String language = radioButton.getText().toString();
                Toast.makeText(this, "Idioma selecionado: " + language, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
