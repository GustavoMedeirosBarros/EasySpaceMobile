package com.example.easyspace.models;

import java.io.Serializable;
import java.util.Date;

public class Reserva implements Serializable {
    private String id;
    private String localId;
    private String localNome;
    private String localImageUrl;
    private String usuarioId;
    private String usuarioNome;
    private String proprietarioId;
    private Date dataInicio;
    private Date dataFim;
    private double valorTotal;
    private String status;
    private long timestamp;
    private String observacoes;
    private int quantidadePessoas;

    public Reserva() {
        this.timestamp = System.currentTimeMillis();
        this.status = "pendente";
    }

    public Reserva(String localId, String localNome, String localImageUrl, String usuarioId,
                   String usuarioNome, String proprietarioId, Date dataInicio, Date dataFim,
                   double valorTotal, int quantidadePessoas) {
        this.localId = localId;
        this.localNome = localNome;
        this.localImageUrl = localImageUrl;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.proprietarioId = proprietarioId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorTotal = valorTotal;
        this.quantidadePessoas = quantidadePessoas;
        this.timestamp = System.currentTimeMillis();
        this.status = "pendente";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getLocalNome() {
        return localNome;
    }

    public void setLocalNome(String localNome) {
        this.localNome = localNome;
    }

    public String getLocalImageUrl() {
        return localImageUrl;
    }

    public void setLocalImageUrl(String localImageUrl) {
        this.localImageUrl = localImageUrl;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getProprietarioId() {
        return proprietarioId;
    }

    public void setProprietarioId(String proprietarioId) {
        this.proprietarioId = proprietarioId;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public int getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(int quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }
}
