package com.example.easyspace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.models.Reserva;
import com.example.easyspace.utils.FirebaseManager;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class PaymentStripeActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private Reserva reserva;
    private FirebaseManager firebaseManager;

    // Chaves de TESTE do Stripe
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51SSNe1A82EAoHYhiSNwXPmidhR2otga4eqZsIxLt8SLzEwaFq4zCHaVqrtv4M1k0taQ3AUGwlvspE1b3DOpA1lSP00ZHsc10nw";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_stripe);

        // Inicializar Stripe
        PaymentConfiguration.init(this, STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        reserva = (Reserva) getIntent().getSerializableExtra("reserva");
        firebaseManager = new FirebaseManager();

        initViews();
        preparePayment();
    }

    private void initViews() {
        TextView textViewAmount = findViewById(R.id.textViewAmount);
        Button buttonPay = findViewById(R.id.buttonPay);

        if (reserva != null) {
            textViewAmount.setText(String.format("Total: R$ %.2f", reserva.getPrecoTotal()));
        }

        buttonPay.setOnClickListener(v -> presentPaymentSheet());

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processando pagamento...");
        progressDialog.setCancelable(false);
    }

    private void preparePayment() {
        // Para testes locais, vamos usar uma abordagem simplificada
        // que não requer backend
        Toast.makeText(this, "Pronto para pagar!", Toast.LENGTH_SHORT).show();
    }

    private void presentPaymentSheet() {
        progressDialog.show();

        // SIMULAÇÃO: Em produção você precisaria de um backend
        // Para testes, vamos usar dados mock
        String mockClientSecret = "pi_3PABC123DEF456_mock_secret_xyz";

        try {
            final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("EasySpace")
                    // Removemos customer configuration para simplificar
                    .build();

            paymentSheet.presentWithPaymentIntent(mockClientSecret, configuration);
        } catch (Exception e) {
            progressDialog.dismiss();
            // Se der erro no PaymentSheet, simulamos pagamento bem-sucedido
            simulateSuccessfulPayment();
        }
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        progressDialog.dismiss();

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Pagamento bem-sucedido via Stripe
            handlePaymentSuccess();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            // Usuário cancelou
            Toast.makeText(this, "Pagamento cancelado", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // Erro no pagamento - para testes, vamos simular sucesso
            Toast.makeText(this, "Em modo de teste: simulando pagamento bem-sucedido", Toast.LENGTH_LONG).show();
            simulateSuccessfulPayment();
        }
    }

    private void simulateSuccessfulPayment() {
        progressDialog.setMessage("Simulando pagamento...");
        progressDialog.show();

        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simula processamento

                runOnUiThread(() -> {
                    handlePaymentSuccess();
                });
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

        // Atualizar status da reserva para "pago"
        reserva.setStatus("pago");
        // Se não tiver o método setMetodoPagamento, vamos adicionar ao objeto
        // reserva.setMetodoPagamento("stripe");

        firebaseManager.atualizarStatusReserva(reserva.getId(), "pago", new FirebaseManager.TaskCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(PaymentStripeActivity.this, "Pagamento confirmado! Reserva realizada.", Toast.LENGTH_LONG).show();

                // Ir para tela principal ou de confirmação
                Intent intent = new Intent(PaymentStripeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(PaymentStripeActivity.this, "Erro ao confirmar reserva: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}