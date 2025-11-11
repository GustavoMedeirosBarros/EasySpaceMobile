package com.example.easyspace;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.easyspace.models.Local;
import com.example.easyspace.models.Reserva;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ReservationActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ImageView imageViewLocal;
    private TextView textViewCategoria, textViewNome;
    private TextView textViewTipoReservaLabel, textViewDatasSelecionadas;
    private MaterialButton buttonSelecionarDatas, buttonConfirmar;
    private TextView textViewSubtotalLabel, textViewSubtotalValor;
    private TextView textViewTaxaValor, textViewTotalValor, textViewTotalValorInferior, textViewDatasInferior;
    private ProgressBar progressBar;

    private LinearLayout layoutHoras;
    private TextInputEditText editTextData, editTextHoraInicio, editTextHoraFim;

    private Local local;
    private FirebaseManager firebaseManager;
    private Reserva novaReserva;
    private final double TAXA_SERVICO_PERCENTUAL = 0.10;
    private Calendar calInicio = Calendar.getInstance();
    private Calendar calFim = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        local = (Local) getIntent().getSerializableExtra("local");
        if (local == null) {
            Toast.makeText(this, "Erro ao carregar local.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseManager = new FirebaseManager();
        novaReserva = new Reserva();
        novaReserva.setTipoLocacao(local.getTipoLocacao());

        initViews();
        populateLocalInfo();
        setupListeners();
    }


    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imageViewLocal = findViewById(R.id.imageViewLocal);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        textViewNome = findViewById(R.id.textViewNome);
        textViewTipoReservaLabel = findViewById(R.id.textViewTipoReservaLabel);
        textViewDatasSelecionadas = findViewById(R.id.textViewDatasSelecionadas);
        buttonSelecionarDatas = findViewById(R.id.buttonSelecionarDatas);
        buttonConfirmar = findViewById(R.id.buttonConfirmar);
        textViewSubtotalLabel = findViewById(R.id.textViewSubtotalLabel);
        textViewSubtotalValor = findViewById(R.id.textViewSubtotalValor);
        textViewTaxaValor = findViewById(R.id.textViewTaxaValor);
        textViewTotalValor = findViewById(R.id.textViewTotalValor);
        textViewTotalValorInferior = findViewById(R.id.textViewTotalValorInferior);
        textViewDatasInferior = findViewById(R.id.textViewDatasInferior);
        progressBar = findViewById(R.id.progressBar);
        layoutHoras = findViewById(R.id.layoutHoras);
        editTextData = findViewById(R.id.editTextData);
        editTextHoraInicio = findViewById(R.id.editTextHoraInicio);
        editTextHoraFim = findViewById(R.id.editTextHoraFim);
    }

    private void populateLocalInfo() {
        textViewCategoria.setText(local.getCategoria());
        textViewNome.setText(local.getNome());
        String imageUrl = local.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
                Glide.with(this).load(imageUrl).centerCrop().into(imageViewLocal);
            } else {
                try {
                    byte[] decodedString = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                    Glide.with(this).load(decodedString).centerCrop().into(imageViewLocal);
                } catch (Exception e) {
                    imageViewLocal.setImageResource(R.drawable.ic_default_space);
                }
            }
        } else {
            imageViewLocal.setImageResource(R.drawable.ic_default_space);
        }
        if (local.getTipoLocacao() != null && local.getTipoLocacao().equalsIgnoreCase("hora")) {
            textViewTipoReservaLabel.setText("Data e Horário");
            textViewDatasSelecionadas.setText("Selecione a data e os horários");
            buttonSelecionarDatas.setVisibility(View.GONE);
            layoutHoras.setVisibility(View.VISIBLE);
        } else {
            textViewTipoReservaLabel.setText("Datas");
            textViewDatasSelecionadas.setText("Selecione as datas da estadia");
            buttonSelecionarDatas.setVisibility(View.VISIBLE);
            layoutHoras.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());
        buttonConfirmar.setOnClickListener(v -> iniciarPagamento());
        buttonSelecionarDatas.setOnClickListener(v -> abrirSeletorDeDatas());
        editTextData.setOnClickListener(v -> abrirSeletorDataUnica());
        editTextHoraInicio.setOnClickListener(v -> abrirSeletorHorario(true));
        editTextHoraFim.setOnClickListener(v -> abrirSeletorHorario(false));
    }

    private void abrirSeletorDeDatas() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Selecione o período");
        builder.setSelection(new androidx.core.util.Pair<>(
                MaterialDatePicker.todayInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds()
        ));
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Long dataInicioUTC = selection.first;
            Long dataFimUTC = selection.second;
            TimeZone tz = TimeZone.getDefault();
            Long offset = (long) tz.getOffset(dataInicioUTC);
            calInicio.setTimeInMillis(dataInicioUTC + offset);
            calFim.setTimeInMillis(dataFimUTC + offset);
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calFim.set(Calendar.HOUR_OF_DAY, 23);
            calFim.set(Calendar.MINUTE, 59);
            novaReserva.setDataInicio(calInicio.getTimeInMillis());
            novaReserva.setDataFim(calFim.getTimeInMillis());
            atualizarResumoPreco();
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER_RANGE");
    }

    private void abrirSeletorDataUnica() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selecione o dia");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker<Long> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            TimeZone tz = TimeZone.getDefault();
            Long offset = (long) tz.getOffset(selection);
            calInicio.setTimeInMillis(selection + offset);
            calFim.setTimeInMillis(selection + offset);
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calFim.set(Calendar.HOUR_OF_DAY, 0);
            calFim.set(Calendar.MINUTE, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editTextData.setText(sdf.format(calInicio.getTime()));
            editTextHoraInicio.setText("");
            editTextHoraFim.setText("");
            atualizarResumoPreco();
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER_SINGLE");
    }

    private void abrirSeletorHorario(boolean isInicio) {
        if (editTextData.getText().toString().isEmpty()) {
            Toast.makeText(this, "Selecione a data primeiro", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calAtual = isInicio ? calInicio : calFim;
        int horaPadrao = calAtual.get(Calendar.HOUR_OF_DAY);
        int minutoPadrao = calAtual.get(Calendar.MINUTE);
        if (horaPadrao == 0 && minutoPadrao == 0) horaPadrao = 8;
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            Calendar calSelecionada = isInicio ? calInicio : calFim;
            calSelecionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSelecionada.set(Calendar.MINUTE, minute);
            if (!isInicio && calFim.before(calInicio)) {
                Toast.makeText(this, "A hora final deve ser após a hora inicial", Toast.LENGTH_SHORT).show();
                editTextHoraFim.setText("");
                buttonConfirmar.setEnabled(false);
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            if (isInicio) {
                editTextHoraInicio.setText(sdf.format(calSelecionada.getTime()));
            } else {
                editTextHoraFim.setText(sdf.format(calSelecionada.getTime()));
            }
            novaReserva.setDataInicio(calInicio.getTimeInMillis());
            novaReserva.setDataFim(calFim.getTimeInMillis());
            atualizarResumoPreco();
        }, horaPadrao, minutoPadrao, true);
        timePicker.show();
    }

    private void atualizarResumoPreco() {
        int quantidade = 0;
        double subtotal = 0;
        double taxaServico = 0;
        double total = 0;
        String labelSubtotal = "R$ 0,00";
        String labelDatasInferior = "Selecione a data";
        Locale br = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(br);

        if (novaReserva.getTipoLocacao() != null && novaReserva.getTipoLocacao().equalsIgnoreCase("hora")) {
            if (novaReserva.getDataInicio() > 0 && novaReserva.getDataFim() > 0 && calFim.after(calInicio)) {
                long diffMillis = calFim.getTimeInMillis() - calInicio.getTimeInMillis();
                long totalMinutos = TimeUnit.MINUTES.convert(diffMillis, TimeUnit.MILLISECONDS);
                int numHoras = (int) (totalMinutos / 60);
                if (totalMinutos % 60 > 0) numHoras++;
                if (numHoras > 0) {
                    quantidade = numHoras;
                    subtotal = local.getPreco() * quantidade;
                    labelSubtotal = String.format(Locale.getDefault(), "%s x %d %s",
                            nf.format(local.getPreco()), quantidade, (quantidade > 1 ? "horas" : "hora"));
                    SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    labelDatasInferior = sdfData.format(calInicio.getTime()) + ", " +
                            sdfHora.format(calInicio.getTime()) + " - " +
                            sdfHora.format(calFim.getTime());
                    textViewDatasSelecionadas.setText(labelDatasInferior);
                }
            }
        } else {
            if (novaReserva.getDataInicio() > 0 && novaReserva.getDataFim() >= novaReserva.getDataInicio()) {
                long diffMillis = calFim.getTimeInMillis() - calInicio.getTimeInMillis();
                int numDias = (int) TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS) + 1;
                quantidade = numDias;
                subtotal = local.getPreco() * quantidade;
                labelSubtotal = String.format(Locale.getDefault(), "%s x %d %s",
                        nf.format(local.getPreco()), quantidade, (quantidade > 1 ? "noites" : "noite"));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                labelDatasInferior = sdf.format(calInicio.getTime()) + " - " + sdf.format(calFim.getTime());
                textViewDatasSelecionadas.setText(labelDatasInferior);
            }
        }

        taxaServico = subtotal * TAXA_SERVICO_PERCENTUAL;
        total = subtotal + taxaServico;
        novaReserva.setQuantidadeDiasHoras(quantidade);
        novaReserva.setPrecoSubtotal(subtotal);
        novaReserva.setTaxaServico(taxaServico);
        novaReserva.setPrecoTotal(total);
        textViewSubtotalLabel.setText(labelSubtotal);
        textViewSubtotalValor.setText(nf.format(subtotal));
        textViewTaxaValor.setText(nf.format(taxaServico));
        textViewTotalValor.setText(nf.format(total));
        textViewTotalValorInferior.setText(nf.format(total));
        textViewDatasInferior.setText(labelDatasInferior);
        buttonConfirmar.setEnabled(total > 0);
    }


    private void iniciarPagamento() {

        progressBar.setVisibility(View.VISIBLE);
        buttonConfirmar.setEnabled(false);

        novaReserva.setLocalId(local.getId());
        novaReserva.setLocalNome(local.getNome());
        novaReserva.setLocalImageUrl(local.getImageUrl());
        novaReserva.setProprietarioId(local.getProprietarioId());
        novaReserva.setUsuarioId(firebaseManager.getCurrentUserId());

        firebaseManager.salvarReserva(novaReserva, new FirebaseManager.TaskCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(ReservationActivity.this, MockPaymentActivity.class);
                intent.putExtra("reserva", novaReserva);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                buttonConfirmar.setEnabled(true);
                Toast.makeText(ReservationActivity.this, "Erro ao criar reserva: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}