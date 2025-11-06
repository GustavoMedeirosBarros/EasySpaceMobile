package com.example.easyspace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class AboutActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView textViewVersion;
    private MaterialCardView cardPrivacyPolicy, cardTerms, cardContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        textViewVersion = findViewById(R.id.textViewVersion);
        cardPrivacyPolicy = findViewById(R.id.cardPrivacyPolicy);
        cardTerms = findViewById(R.id.cardTerms);
        cardContact = findViewById(R.id.cardContact);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sobre");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        cardPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://easyspace.com/privacy"));
        });

        cardTerms.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://easyspace.com/terms"));
        });

        cardContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:contato@easyspace.com"));
            startActivity(Intent.createChooser(intent, "Enviar email"));
        });
    }
}
