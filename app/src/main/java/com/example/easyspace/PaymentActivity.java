package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.network.MercadoPagoService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "EasySpacePay";
    private static final String MP_PUBLIC_KEY = "TEST-931c057b-7169-49c3-9964-c9a3163771ae";
    private static final String BACKEND_URL = "https://easyspacebackendmobile.vercel.app/";

    private TextInputEditText editCardNumber, editExpiry, editCvv, editHolderName, editDocNumber;
    private MaterialButton btnPay;
    private ProgressBar progressBar;

    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();

        amount = getIntent().getDoubleExtra("valor", 0.0);
        btnPay.setText(String.format("Pagar R$ %.2f", amount));

        btnPay.setOnClickListener(v -> startPaymentFlow());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        editCardNumber = findViewById(R.id.editCardNumber);
        editExpiry = findViewById(R.id.editExpiry);
        editCvv = findViewById(R.id.editCvv);
        editHolderName = findViewById(R.id.editHolderName);
        editDocNumber = findViewById(R.id.editDocNumber);
        btnPay = findViewById(R.id.btnPay);
        progressBar = findViewById(R.id.progressBar);
    }

    private void startPaymentFlow() {
        String rawCard = editCardNumber.getText().toString();
        String cleanCard = rawCard.replace(" ", "").replace("-", "");
        String cvv = editCvv.getText().toString();

        Log.d(TAG, "Iniciando validação local...");
        Log.d(TAG, "Cartão digitado: " + rawCard);
        Log.d(TAG, "Cartão limpo: " + cleanCard + " (Tamanho: " + cleanCard.length() + ")");
        Log.d(TAG, "CVV: " + cvv);

        if (cleanCard.length() < 15) {
            Toast.makeText(this, "Número do cartão incompleto ("+cleanCard.length()+" dígitos)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cvv.length() < 3) {
            Toast.makeText(this, "CVV inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        tokenizeCard();
    }

    private void tokenizeCard() {
        Log.d(TAG, "Preparando tokenização para Mercado Pago...");

        Retrofit retrofitMP = new Retrofit.Builder()
                .baseUrl("https://api.mercadopago.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MercadoPagoService mpService = retrofitMP.create(MercadoPagoService.class);

        JsonObject cardData = new JsonObject();
        cardData.addProperty("card_number", editCardNumber.getText().toString().replace(" ", ""));

        JsonObject cardholder = new JsonObject();
        cardholder.addProperty("name", editHolderName.getText().toString());

        JsonObject doc = new JsonObject();
        doc.addProperty("type", "CPF");
        doc.addProperty("number", editDocNumber.getText().toString().replace(".", "").replace("-", ""));

        cardholder.add("identification", doc);

        cardData.add("cardholder", cardholder);

        String expiry = editExpiry.getText().toString();
        try {
            if(expiry.contains("/")) {
                String[] parts = expiry.split("/");
                cardData.addProperty("expiration_month", Integer.parseInt(parts[0]));
                String year = parts[1];
                if (year.length() == 2) year = "20" + year;
                cardData.addProperty("expiration_year", Integer.parseInt(year));
            } else {
                String month = expiry.substring(0, 2);
                String year = "20" + expiry.substring(2);
                cardData.addProperty("expiration_month", Integer.parseInt(month));
                cardData.addProperty("expiration_year", Integer.parseInt(year));
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar data: " + e.getMessage());
            setLoading(false);
            Toast.makeText(this, "Data inválida. Use MM/AA", Toast.LENGTH_SHORT).show();
            return;
        }

        cardData.addProperty("security_code", editCvv.getText().toString());

        Log.d(TAG, "Enviando JSON para MP: " + cardData.toString());

        mpService.createCardToken(MP_PUBLIC_KEY, cardData).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String cardToken = response.body().get("id").getAsString();
                    Log.d(TAG, "Token criado com sucesso: " + cardToken);
                    processPaymentInBackend(cardToken);
                } else {
                    setLoading(false);
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Erro API MP (" + response.code() + "): " + errorBody);
                        Toast.makeText(PaymentActivity.this, "MP recusou: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Falha na conexão MP: " + t.getMessage());
                Toast.makeText(PaymentActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPaymentInBackend(String cardToken) {
        Log.d(TAG, "Enviando token para Backend Vercel...");

        Retrofit retrofitBackend = new Retrofit.Builder()
                .baseUrl(BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MercadoPagoService backendService = retrofitBackend.create(MercadoPagoService.class);

        JsonObject paymentBody = new JsonObject();
        paymentBody.addProperty("token", cardToken);
        paymentBody.addProperty("transaction_amount", amount);
        paymentBody.addProperty("installments", 1);

        String numeroCartao = editCardNumber.getText().toString().replace(" ", "").replace("-", "");
        String metodoPagamento = getPaymentMethodId(numeroCartao);
        Log.d(TAG, "Bandeira detectada: " + metodoPagamento);

        paymentBody.addProperty("payment_method_id", metodoPagamento);

        paymentBody.addProperty("payer_email", "test_user_123@testuser.com");
        paymentBody.addProperty("payer_doc_type", "CPF");

        String cpfLimpo = editDocNumber.getText().toString().replace(".", "").replace("-", "");
        paymentBody.addProperty("payer_doc_number", cpfLimpo);

        backendService.processPayment(paymentBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Resposta Backend: " + response.body().toString());
                    String status = response.body().get("status").getAsString();

                    if ("approved".equals(status)) {
                        Toast.makeText(PaymentActivity.this, "Aprovado!", Toast.LENGTH_LONG).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("pagamentoConfirmado", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        String detail = "";
                        if(response.body().has("status_detail"))
                            detail = response.body().get("status_detail").getAsString();

                        String mensagemErro;
                        if ("cc_rejected_duplicated_payment".equals(detail)) {
                            mensagemErro = "Pagamento duplicado. Aguarde ou mude o valor.";
                        } else if ("cc_rejected_other_reason".equals(detail)) {
                            mensagemErro = "Cartão recusado pelo banco.";
                        } else {
                            mensagemErro = "Pagamento recusado. Tente outro cartão.";
                        }

                        Log.w(TAG, "Pagamento recusado: " + detail);
                        Toast.makeText(PaymentActivity.this, mensagemErro, Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String errorInfo = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "Erro Backend (" + response.code() + "): " + errorInfo);
                        Toast.makeText(PaymentActivity.this, "Erro no processamento do pagamento", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Falha conexão Backend: " + t.getMessage());
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPay.setEnabled(!loading);
        editCardNumber.setEnabled(!loading);
    }

    private String getPaymentMethodId(String number) {
        if (number == null || number.isEmpty()) return "master";

        String cleanNumber = number.replace(" ", "").replace("-", "");

        if (cleanNumber.startsWith("4")) {
            return "visa";
        }
        else if (cleanNumber.startsWith("34") || cleanNumber.startsWith("37")) {
            return "amex";
        }
        else if (cleanNumber.startsWith("6") || cleanNumber.startsWith("5067") || cleanNumber.startsWith("5090") || cleanNumber.startsWith("4011") || cleanNumber.startsWith("4389") || cleanNumber.startsWith("4514") || cleanNumber.startsWith("4576")) {
            return "elo";
        }
        else if (cleanNumber.startsWith("5")) {
            return "master";
        }
        else if (cleanNumber.startsWith("30") || cleanNumber.startsWith("36") || cleanNumber.startsWith("38")) {
            return "diners";
        }
        else if (cleanNumber.startsWith("6062")) {
            return "hipercard";
        }

        return "master";
    }
}