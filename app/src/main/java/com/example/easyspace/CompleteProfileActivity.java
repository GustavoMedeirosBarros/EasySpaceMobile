package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.utils.FirebaseManager;
import com.example.easyspace.utils.MaskTextWatcher;
import com.example.easyspace.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {

    private TextInputEditText editTextTelefone, editTextCEP, editTextRua, editTextNumero;
    private TextInputEditText editTextComplemento, editTextBairro, editTextCidade, editTextEstado, editTextCPF;
    private MaterialButton buttonConcluir;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        firebaseManager = new FirebaseManager();
        initViews();
        setupListeners();
    }

    private void initViews() {
        editTextTelefone = findViewById(R.id.editTextTelefone);
        editTextCEP = findViewById(R.id.editTextCEP);
        editTextRua = findViewById(R.id.editTextRua);
        editTextNumero = findViewById(R.id.editTextNumero);
        editTextComplemento = findViewById(R.id.editTextComplemento);
        editTextBairro = findViewById(R.id.editTextBairro);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
        editTextCPF = findViewById(R.id.editTextCPF);
        buttonConcluir = findViewById(R.id.buttonConcluir);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        buttonConcluir.setOnClickListener(v -> salvarDadosAdicionais());

        editTextTelefone.addTextChangedListener(new MaskTextWatcher(editTextTelefone, "(##) #####-####"));
        editTextCEP.addTextChangedListener(new MaskTextWatcher(editTextCEP, "#####-###"));
        editTextCPF.addTextChangedListener(new MaskTextWatcher(editTextCPF, "###.###.###-##"));

        editTextCEP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cep = MaskTextWatcher.unmask(s.toString());
                if (cep.length() == 8) {
                    buscarCEP(cep);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void buscarCEP(String cep) {
        progressBar.setVisibility(View.VISIBLE);

        ValidationUtils.buscarCEP(cep, new ValidationUtils.CepCallback() {
            @Override
            public void onSuccess(String endereco, String bairro, String cidade, String estado) {
                runOnUiThread(() -> {
                    editTextRua.setText(endereco);
                    editTextBairro.setText(bairro);
                    editTextCidade.setText(cidade);
                    editTextEstado.setText(estado);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(CompleteProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void salvarDadosAdicionais() {
        String telefone = MaskTextWatcher.unmask(editTextTelefone.getText().toString().trim());
        String cep = MaskTextWatcher.unmask(editTextCEP.getText().toString().trim());
        String cpf = MaskTextWatcher.unmask(editTextCPF.getText().toString().trim());
        String rua = ValidationUtils.sanitizeInput(editTextRua.getText().toString().trim());
        String numero = ValidationUtils.sanitizeInput(editTextNumero.getText().toString().trim());
        String complemento = ValidationUtils.sanitizeInput(editTextComplemento.getText().toString().trim());
        String bairro = ValidationUtils.sanitizeInput(editTextBairro.getText().toString().trim());
        String cidade = ValidationUtils.sanitizeInput(editTextCidade.getText().toString().trim());
        String estado = ValidationUtils.sanitizeInput(editTextEstado.getText().toString().trim());

        if (telefone.isEmpty()) {
            editTextTelefone.setError("Telefone é obrigatório");
            editTextTelefone.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPhone(telefone)) {
            editTextTelefone.setError("Telefone inválido");
            editTextTelefone.requestFocus();
            return;
        }

        if (cep.isEmpty()) {
            editTextCEP.setError("CEP é obrigatório");
            editTextCEP.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidCEP(cep)) {
            editTextCEP.setError("CEP inválido");
            editTextCEP.requestFocus();
            return;
        }

        if (rua.isEmpty()) {
            editTextRua.setError("Rua é obrigatória");
            editTextRua.requestFocus();
            return;
        }

        if (numero.isEmpty()) {
            editTextNumero.setError("Número é obrigatório");
            editTextNumero.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidAddressNumber(numero)) {
            editTextNumero.setError("Número inválido");
            editTextNumero.requestFocus();
            return;
        }

        if (cidade.isEmpty()) {
            editTextCidade.setError("Cidade é obrigatória");
            editTextCidade.requestFocus();
            return;
        }

        if (estado.isEmpty()) {
            editTextEstado.setError("Estado é obrigatório");
            editTextEstado.requestFocus();
            return;
        }

        if (cpf.isEmpty()) {
            editTextCPF.setError("CPF é obrigatório");
            editTextCPF.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidCPF(cpf)) {
            editTextCPF.setError("CPF inválido");
            editTextCPF.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonConcluir.setEnabled(false);

        String enderecoCompleto = rua + ", " + numero +
                (complemento.isEmpty() ? "" : " - " + complemento) +
                " - " + bairro + ", " + cidade + " - " + estado + ", " + cep;

        Map<String, Object> dadosAdicionais = new HashMap<>();
        dadosAdicionais.put("telefone", telefone);
        dadosAdicionais.put("cep", cep);
        dadosAdicionais.put("rua", rua);
        dadosAdicionais.put("numero", numero);
        dadosAdicionais.put("complemento", complemento);
        dadosAdicionais.put("bairro", bairro);
        dadosAdicionais.put("cidade", cidade);
        dadosAdicionais.put("estado", estado);
        dadosAdicionais.put("cpf", cpf);
        dadosAdicionais.put("endereco", enderecoCompleto);
        dadosAdicionais.put("profileComplete", true);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseManager.updateUserData(userId, dadosAdicionais, new FirebaseManager.UpdateCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CompleteProfileActivity.this,
                        "Perfil completado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CompleteProfileActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                buttonConcluir.setEnabled(true);
                Toast.makeText(CompleteProfileActivity.this,
                        "Erro ao salvar dados. Tente novamente", Toast.LENGTH_LONG).show();
            }
        });
    }
}
