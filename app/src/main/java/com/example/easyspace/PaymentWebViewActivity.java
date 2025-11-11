package com.example.easyspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.models.Reserva;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;

public class PaymentWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;
    private String preferenceId;
    private Reserva reserva;
    private FirebaseManager firebaseManager;

    // URLs de redirecionamento (você pode configurar isso no Postman)
    private static final String SUCCESS_URL = "https://www.easyspace.com/success";
    private static final String FAILURE_URL = "https://www.easyspace.com/failure";
    private static final String PENDING_URL = "https://www.easyspace.com/pending";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        preferenceId = getIntent().getStringExtra("preferenceId");
        reserva = (Reserva) getIntent().getSerializableExtra("reserva");
        firebaseManager = new FirebaseManager();

        if (preferenceId == null || reserva == null) {
            Toast.makeText(this, "Erro: ID da preferência ou reserva não encontrado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupWebView();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            // Cancela a reserva se o usuário fechar a janela
            handlePaymentCancelled();
        });
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(SUCCESS_URL)) {
                    handlePaymentSuccess();
                    return true; // Impede o carregamento da URL
                } else if (url.startsWith(FAILURE_URL)) {
                    handlePaymentError();
                    return true;
                } else if (url.startsWith(PENDING_URL)) {
                    handlePaymentPending();
                    return true;
                }
                // Carrega a URL do Mercado Pago
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        // Este é o link do sandbox. O init_point de produção não teria "sandbox."
        String paymentUrl = "https://sandbox.mercadopago.com.br/checkout/v1/redirect?pref_id=" + preferenceId;
        webView.loadUrl(paymentUrl);
    }

    private void handlePaymentSuccess() {
        reserva.setStatus("confirmed");
        firebaseManager.updateReservaStatus(reserva.getId(), "confirmed", new FirebaseManager.TaskCallback() {
            @Override
            public void onSuccess() {
                firebaseManager.sendInAppNotification(
                        reserva.getProprietarioId(),
                        "Nova Reserva!",
                        "Seu espaço '" + reserva.getLocalNome() + "' foi reservado.",
                        new FirebaseManager.TaskCallback() {
                            @Override public void onSuccess() {}
                            @Override public void onFailure(String error) {}
                        }
                );
                launchConfirmationActivity(true);
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(PaymentWebViewActivity.this, "Pagamento aprovado, mas falha ao confirmar reserva.", Toast.LENGTH_LONG).show();
                launchConfirmationActivity(true);
            }
        });
    }

    private void handlePaymentCancelled() {
        // Deleta a reserva "pendente" que foi cancelada
        firebaseManager.deleteReserva(reserva.getId(), new FirebaseManager.TaskCallback() {
            @Override public void onSuccess() {}
            @Override public void onFailure(String error) {}
        });
        Toast.makeText(this, "Pagamento cancelado", Toast.LENGTH_SHORT).show();
        finish(); // Fecha a WebView e volta para a tela de detalhes
    }

    private void handlePaymentPending() {
        // Não deleta, mas avisa o usuário.
        Toast.makeText(this, "Pagamento pendente. Aguardando confirmação.", Toast.LENGTH_LONG).show();
        launchConfirmationActivity(true); // Trata como sucesso por enquanto
    }

    private void handlePaymentError() {
        // Deleta a reserva "pendente" que falhou
        firebaseManager.deleteReserva(reserva.getId(), new FirebaseManager.TaskCallback() {
            @Override public void onSuccess() {}
            @Override public void onFailure(String error) {}
        });
        Toast.makeText(this, "Erro no pagamento", Toast.LENGTH_LONG).show();
        launchConfirmationActivity(false);
    }

    private void launchConfirmationActivity(boolean success) {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("reserva", reserva);
        intent.putExtra("success", success);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}