package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspace.utils.UserManager;
import com.example.easyspace.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;


public class RegisterActivity extends AppCompatActivity {
    private EditText editTextNome, editTextEmail, editTextSenha, editTextConfirmarSenha,
            editTextTelefone, editTextDocumento, editTextCEP, editTextCidade, editTextEstado;
    private RadioGroup radioGroupTipoUsuario;

    private TextInputLayout textInputLayoutDocumento;
    private RadioButton radioButtonPessoaFisica, radioButtonPessoaJuridica;
    private MaterialButton buttonRegistrar, buttonGoogleRegister;
    private TextView textViewLogin;
    private ImageView imageViewClose;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = new UserManager(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        editTextNome = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextConfirmarSenha = findViewById(R.id.editTextConfirmarSenha);
        editTextTelefone = findViewById(R.id.editTextTelefone);
        editTextDocumento = findViewById(R.id.editTextDocumento);
        editTextCEP = findViewById(R.id.editTextCEP);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
        radioGroupTipoUsuario = findViewById(R.id.radioGroupTipoUsuario);
        radioButtonPessoaFisica = findViewById(R.id.radioButtonPessoaFisica);
        radioButtonPessoaJuridica = findViewById(R.id.radioButtonPessoaJuridica);
        buttonRegistrar = findViewById(R.id.buttonRegistrar);
        buttonGoogleRegister = findViewById(R.id.buttonGoogleRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        imageViewClose = findViewById(R.id.imageViewClose);
        textInputLayoutDocumento = findViewById(R.id.textInputLayoutDocumento);
        editTextDocumento = findViewById(R.id.editTextDocumento);
    }

    private void setupListeners() {
        imageViewClose.setOnClickListener(v -> finish());

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        buttonGoogleRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Registro com Google em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        radioGroupTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            editTextDocumento.setText("");
            editTextDocumento.setError(null);
            editTextDocumento.clearFocus();

            if (checkedId == R.id.radioButtonPessoaFisica) {
                textInputLayoutDocumento.setHint("CPF");
                editTextDocumento.setHint("CPF");
            } else {
                textInputLayoutDocumento.setHint("CNPJ");
                editTextDocumento.setHint("CNPJ");
            }
        });

        editTextCEP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cep = s.toString().replaceAll("[^0-9]", "");
                if (cep.length() == 8) {
                    buscarCEP(cep);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void buscarCEP(String cep) {
        ValidationUtils.buscarCEP(cep, new ValidationUtils.CEPCallback() {
            @Override
            public void onSuccess(String cidade, String estado) {
                editTextCidade.setText(cidade);
                editTextEstado.setText(estado);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegisterActivity.this, "Erro ao buscar CEP: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarUsuario() {
        String nome = editTextNome.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString();
        String confirmarSenha = editTextConfirmarSenha.getText().toString();
        String telefone = editTextTelefone.getText().toString().trim();
        String documento = editTextDocumento.getText().toString().trim();
        String cep = editTextCEP.getText().toString().trim();
        String cidade = editTextCidade.getText().toString().trim();
        String estado = editTextEstado.getText().toString().trim();

        if (nome.isEmpty()) {
            editTextNome.setError("Nome é obrigatório");
            editTextNome.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email é obrigatório");
            editTextEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            editTextEmail.setError("Email inválido");
            editTextEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            editTextSenha.setError("Senha é obrigatória");
            editTextSenha.requestFocus();
            return;
        }

        if (senha.length() < 6) {
            editTextSenha.setError("Senha deve ter pelo menos 6 caracteres");
            editTextSenha.requestFocus();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            editTextConfirmarSenha.setError("Senhas não coincidem");
            editTextConfirmarSenha.requestFocus();
            return;
        }

        if (telefone.isEmpty()) {
            editTextTelefone.setError("Telefone é obrigatório");
            editTextTelefone.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPhone(telefone)) {
            editTextTelefone.setError("Telefone inválido. Use formato: (11) 99999-9999");
            editTextTelefone.requestFocus();
            return;
        }

        if (documento.isEmpty()) {
            editTextDocumento.setError("Documento é obrigatório");
            editTextDocumento.requestFocus();
            return;
        }

        boolean isPessoaFisica = radioButtonPessoaFisica.isChecked();
        if (isPessoaFisica && !ValidationUtils.isValidCPF(documento)) {
            editTextDocumento.setError("CPF inválido");
            editTextDocumento.requestFocus();
            return;
        }

        if (!isPessoaFisica && !ValidationUtils.isValidCNPJ(documento)) {
            editTextDocumento.setError("CNPJ inválido");
            editTextDocumento.requestFocus();
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

        if (userManager.emailExists(email)) {
            editTextEmail.setError("Este email já está cadastrado");
            editTextEmail.requestFocus();
            return;
        }

        boolean success = userManager.registerUser(nome, email, senha, telefone, documento, cep, cidade, estado, isPessoaFisica);

        if (success) {
            Toast.makeText(this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show();
        }
    }
}
