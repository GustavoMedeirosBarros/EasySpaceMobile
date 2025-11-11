package com.example.easyspace.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reserva implements Serializable {

    private String id;
    private String localId;
    private String localNome;
    private String localImageUrl;
    private String usuarioId;
    private String proprietarioId;
    private long dataInicio;
    private long dataFim;
    private double precoSubtotal;
    private double taxaServico;
    private double precoTotal;
    private String status; // Ex: "pending", "confirmed", "cancelled"
    private long dataCriacao;
    private String tipoLocacao; // "dia" ou "hora"
    private int quantidadeDiasHoras;

    private String metodoPagamento;

    public Reserva() {
        this.dataCriacao = System.currentTimeMillis();
        this.status = "pending";
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }
    public String getLocalNome() { return localNome; }
    public void setLocalNome(String localNome) { this.localNome = localNome; }
    public String getLocalImageUrl() { return localImageUrl; }
    public void setLocalImageUrl(String localImageUrl) { this.localImageUrl = localImageUrl; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(String proprietarioId) { this.proprietarioId = proprietarioId; }
    public long getDataInicio() { return dataInicio; }
    public void setDataInicio(long dataInicio) { this.dataInicio = dataInicio; }
    public long getDataFim() { return dataFim; }
    public void setDataFim(long dataFim) { this.dataFim = dataFim; }
    public double getPrecoSubtotal() { return precoSubtotal; }
    public void setPrecoSubtotal(double precoSubtotal) { this.precoSubtotal = precoSubtotal; }
    public double getTaxaServico() { return taxaServico; }
    public void setTaxaServico(double taxaServico) { this.taxaServico = taxaServico; }
    public double getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(double precoTotal) { this.precoTotal = precoTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(long dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getTipoLocacao() { return tipoLocacao; }
    public void setTipoLocacao(String tipoLocacao) { this.tipoLocacao = tipoLocacao; }
    public int getQuantidadeDiasHoras() { return quantidadeDiasHoras; }
    public void setQuantidadeDiasHoras(int quantidadeDiasHoras) { this.quantidadeDiasHoras = quantidadeDiasHoras; }

    // Métodos utilitários para exibição
    public String getDatasFormatadas() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String inicio = sdf.format(new Date(dataInicio));
        String fim = sdf.format(new Date(dataFim));

        if (tipoLocacao.equalsIgnoreCase("hora")) {
            SimpleDateFormat sdft = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            inicio = sdft.format(new Date(dataInicio));
            fim = sdft.format(new Date(dataFim));
            return inicio + " - " + fim.split(" ")[1]; // Ex: 10/11 09:00 - 11:00
        }

        if (inicio.equals(fim)) {
            return inicio; // Apenas um dia
        }
        return inicio + " - " + fim;
    }

    public String getPrecoTotalFormatado() {
        return String.format(Locale.getDefault(), "R$ %.2f", precoTotal);
    }

    public String getStatusFormatado() {
        if (status == null) return "N/A";
        switch (status) {
            case "confirmed":
                return "Confirmada";
            case "pending":
                return "Pendente";
            case "cancelled":
                return "Cancelada";
            default:
                return status;
        }
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}