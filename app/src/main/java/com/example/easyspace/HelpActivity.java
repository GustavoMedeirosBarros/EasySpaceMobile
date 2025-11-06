package com.example.easyspace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialCardView cardFaq, cardSupport, cardWhatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cardFaq = findViewById(R.id.cardFaq);
        cardSupport = findViewById(R.id.cardSupport);
        cardWhatsapp = findViewById(R.id.cardWhatsapp);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Central de Ajuda");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        cardFaq.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://easyspace.com/faq"));
        });

        cardSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:suporte@easyspace.com"));
            startActivity(Intent.createChooser(intent, "Enviar email"));
        });

        cardWhatsapp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/5511999999999"));
            startActivity(intent);
        });
    }
}
