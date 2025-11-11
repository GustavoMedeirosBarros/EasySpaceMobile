package com.example.easyspace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.models.Reserva;
import com.example.easyspace.utils.FirebaseManager;
import com.example.easyspace.utils.MaskTextWatcher;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;


public class MockPaymentActivity extends AppCompatActivity {

    private Reserva reserva;
    private FirebaseManager firebaseManager;

    private ProgressDialog progressDialog;
    private MaterialToolbar toolbar;
    private TextView textViewAmount;
    private Button buttonPay;
    private TextInputEditText editTextCardNumber, editTextExpiryDate, editTextCVV, editTextCardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_payment);

        reserva = (Reserva) getIntent().getSerializableExtra("reserva");
        if (reserva == null) {
            Toast.makeText(this, "Erro: Reserva não encontrada.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        firebaseManager = new FirebaseManager();

        initViews();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        textViewAmount = findViewById(R.id.textViewAmount);
        buttonPay = findViewById(R.id.buttonPay);

        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextCVV = findViewById(R.id.editTextCVV);
        editTextCardName = findViewById(R.id.editTextCardName);

        editTextCardNumber.addTextChangedListener(new MaskTextWatcher(editTextCardNumber, "#### #### #### ####"));
        editTextExpiryDate.addTextChangedListener(new MaskTextWatcher(editTextExpiryDate, "##/##"));
        editTextCVV.addTextChangedListener(new MaskTextWatcher(editTextCVV, "###"));


        if (reserva != null) {
            textViewAmount.setText(String.format("Total: R$ %.2f", reserva.getPrecoTotal()));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processando pagamento...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());
        buttonPay.setOnClickListener(v -> validateAndPay());
    }

    private void validateAndPay() {
        if (TextUtils.isEmpty(editTextCardNumber.getText()) ||
                TextUtils.isEmpty(editTextExpiryDate.getText()) ||
                TextUtils.isEmpty(editTextCVV.getText()) ||
                TextUtils.isEmpty(editTextCardName.getText())) {

            Toast.makeText(this, "Preencha todos os dados do cartão", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextCardNumber.getText().length() < 19) {
            Toast.makeText(this, "Número do cartão inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextExpiryDate.getText().length() < 5) {
            Toast.makeText(this, "Data de validade inválida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextCVV.getText().length() < 3) {
            Toast.makeText(this, "CVV inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        simulateSuccessfulPayment();
    }

    private void simulateSuccessfulPayment() {
        progressDialog.show();

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                runOnUiThread(this::handlePaymentSuccess);
            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Erro na simulação", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void handlePaymentSuccess() {
        progressDialog.setMessage("Confirmando reserva...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        reserva.setStatus("pago");
        reserva.setMetodoPagamento("simulado_cartao");

        firebaseManager.atualizarStatusReserva(reserva.getId(), "pago", new FirebaseManager.TaskCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(MockPaymentActivity.this, "Pagamento confirmado! Reserva realizada.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MockPaymentActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(MockPaymentActivity.this, "Erro ao confirmar reserva: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}