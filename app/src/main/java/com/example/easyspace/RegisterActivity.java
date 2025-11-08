package com.example.easyspace;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyspace.utils.FirebaseManager;
import com.example.easyspace.utils.ValidationUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextNome, editTextEmail, editTextSenha, editTextConfirmarSenha,
            editTextTelefone, editTextDocumento, editTextCEP, editTextCidade, editTextEstado,
            editTextEndereco, editTextComplemento, editTextBairro, editTextDataNascimento;
    private AutoCompleteTextView autoCompleteGenero;
    private RadioGroup radioGroupTipoUsuario;
    private RadioButton radioButtonPessoaFisica, radioButtonPessoaJuridica;
    private MaterialButton buttonRegistrar, buttonGoogleRegister;
    private TextView textViewLogin;
    private ImageView imageViewClose;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseManager = new FirebaseManager();
        firebaseManager.configureGoogleSignIn(this);

        initViews();
        setupListeners();
        setupBottomNavigation();
        setupGeneroDropdown();
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
        editTextEndereco = findViewById(R.id.editTextEndereco);
        editTextComplemento = findViewById(R.id.editTextComplemento);
        editTextBairro = findViewById(R.id.editTextBairro);
        editTextDataNascimento = findViewById(R.id.editTextDataNascimento);
        autoCompleteGenero = findViewById(R.id.autoCompleteGenero);
        radioGroupTipoUsuario = findViewById(R.id.radioGroupTipoUsuario);
        radioButtonPessoaFisica = findViewById(R.id.radioButtonPessoaFisica);
        radioButtonPessoaJuridica = findViewById(R.id.radioButtonPessoaJuridica);
        buttonRegistrar = findViewById(R.id.buttonRegistrar);
        buttonGoogleRegister = findViewById(R.id.buttonGoogleRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        imageViewClose = findViewById(R.id.imageViewClose);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupListeners() {
        imageViewClose.setOnClickListener(v -> finish());

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        buttonGoogleRegister.setOnClickListener(v -> signInWithGoogle());

        radioGroupTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonPessoaFisica) {
                editTextDocumento.setHint("CPF");
            } else {
                editTextDocumento.setHint("CNPJ");
            }
        });

        editTextDataNascimento.setOnClickListener(v -> showDatePicker());

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

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void setupGeneroDropdown() {
        String[] generos = {"Masculino", "Feminino", "Outro", "Prefiro não informar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, generos);
        autoCompleteGenero.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editTextDataNascimento.setText(date);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void signInWithGoogle() {
        Intent signInIntent = firebaseManager.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            progressBar.setVisibility(View.VISIBLE);

            firebaseManager.signInWithGoogle(task, new FirebaseManager.AuthCallback() {
                @Override
                public void onSuccess(String userId) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Login com Google realizado!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void buscarCEP(String cep) {
        ValidationUtils.buscarCEP(cep, new ValidationUtils.CepCallback() {
            @Override
            public void onSuccess(String endereco, String bairro, String cidade, String estado) {
                runOnUiThread(() -> {
                    editTextEndereco.setText(endereco);
                    editTextCidade.setText(cidade);
                    editTextEstado.setText(estado);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });

    }

    private void registrarUsuario() {
        String nome = ValidationUtils.sanitizeInput(editTextNome.getText().toString().trim());
        String email = ValidationUtils.sanitizeInput(editTextEmail.getText().toString().trim().toLowerCase());
        String senha = editTextSenha.getText().toString();
        String confirmarSenha = editTextConfirmarSenha.getText().toString();
        String telefone = ValidationUtils.sanitizeInput(editTextTelefone.getText().toString().trim());
        String documento = ValidationUtils.sanitizeInput(editTextDocumento.getText().toString().trim());
        String cep = ValidationUtils.sanitizeInput(editTextCEP.getText().toString().trim());
        String cidade = ValidationUtils.sanitizeInput(editTextCidade.getText().toString().trim());
        String estado = ValidationUtils.sanitizeInput(editTextEstado.getText().toString().trim());
        String endereco = ValidationUtils.sanitizeInput(editTextEndereco.getText().toString().trim());
        String complemento = ValidationUtils.sanitizeInput(editTextComplemento.getText().toString().trim());
        String bairro = ValidationUtils.sanitizeInput(editTextBairro.getText().toString().trim());
        String dataNascimento = ValidationUtils.sanitizeInput(editTextDataNascimento.getText().toString().trim());
        String genero = ValidationUtils.sanitizeInput(autoCompleteGenero.getText().toString().trim());

        if (nome.isEmpty()) {
            editTextNome.setError("Nome é obrigatório");
            editTextNome.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidName(nome)) {
            editTextNome.setError("Nome inválido. Use apenas letras, espaços e hífens");
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

        ValidationUtils.PasswordStrength passwordStrength = ValidationUtils.validatePasswordStrength(senha);
        if (!passwordStrength.isValid) {
            editTextSenha.setError(passwordStrength.message);
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
            editTextTelefone.setError("Telefone inválido");
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

        if (endereco.isEmpty()) {
            editTextEndereco.setError("Endereço é obrigatório");
            editTextEndereco.requestFocus();
            return;
        }

        if (bairro.isEmpty()) {
            editTextBairro.setError("Bairro é obrigatório");
            editTextBairro.requestFocus();
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

        if (dataNascimento.isEmpty()) {
            editTextDataNascimento.setError("Data de nascimento é obrigatória");
            editTextDataNascimento.requestFocus();
            return;
        }

        if (genero.isEmpty()) {
            autoCompleteGenero.setError("Gênero é obrigatório");
            autoCompleteGenero.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonRegistrar.setEnabled(false);

        firebaseManager.registerUser(nome, email, senha, telefone, documento, cep, cidade, estado,
                isPessoaFisica, endereco, complemento, bairro, dataNascimento, genero,
                new FirebaseManager.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        progressBar.setVisibility(View.GONE);
                        buttonRegistrar.setEnabled(true);

                        String errorMessage = "Erro ao registrar. Tente novamente";
                        if (error.contains("email address is already in use")) {
                            errorMessage = "Este email já está cadastrado";
                        } else if (error.contains("network")) {
                            errorMessage = "Erro de conexão. Verifique sua internet";
                        }

                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
