package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.easyspace.models.Reserva;
import com.google.android.material.button.MaterialButton;

public class ConfirmationActivity extends AppCompatActivity {

    private ImageView imageViewStatus;
    private TextView textViewStatusTitulo, textViewStatusMensagem;
    private MaterialButton buttonVerReserva, buttonVoltarHome;
    private Reserva reserva;
    private boolean isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        reserva = (Reserva) getIntent().getSerializableExtra("reserva");
        isSuccess = getIntent().getBooleanExtra("success", false);

        initViews();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        imageViewStatus = findViewById(R.id.imageViewStatus);
        textViewStatusTitulo = findViewById(R.id.textViewStatusTitulo);
        textViewStatusMensagem = findViewById(R.id.textViewStatusMensagem);
        buttonVerReserva = findViewById(R.id.buttonVerReserva);
        buttonVoltarHome = findViewById(R.id.buttonVoltarHome);
    }

    private void setupUI() {
        if (isSuccess) {
            textViewStatusTitulo.setText("Reserva Confirmada!");
            textViewStatusMensagem.setText("Sua reserva para '" + (reserva != null ? reserva.getLocalNome() : "") + "' foi confirmada.");
            buttonVerReserva.setText("Ver minhas reservas");
        } else {
            textViewStatusTitulo.setText("Pagamento Falhou");
            textViewStatusMensagem.setText("Não foi possível processar seu pagamento. Por favor, tente novamente.");
            buttonVerReserva.setText("Tentar novamente");
        }
    }

    private void setupListeners() {
        buttonVoltarHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        buttonVerReserva.setOnClickListener(v -> {
            if (isSuccess) {
                startActivity(new Intent(this, MinhasReservasActivity.class));
            } else {
                Intent intent = new Intent(this, LocalDetailActivity.class);
                intent.putExtra("localId", (reserva != null ? reserva.getLocalId() : null));
                startActivity(intent);
            }
            finish();
        });
    }
}