package com.example.easyspace.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Local implements Serializable {
    private String id;
    private String nome;
    private String endereco;
    private double preco;
    private double rating;
    private String categoria;
    private String imageUrl;
    private String descricao;
    private int capacidade;
    private String horarioFuncionamento;
    private List<String> comodidades;
    private double latitude;
    private double longitude;
    private String tipoLocacao;
    private String proprietarioId;
    private long timestamp;
    private int viewCount;

    public Local() {
        this.comodidades = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.viewCount = 0;
    }

    public Local(String nome, String endereco, double preco, double rating, String categoria, String imageUrl) {
        this.nome = nome;
        this.endereco = endereco;
        this.preco = preco;
        this.rating = rating;
        this.categoria = categoria;
        this.setImageUrl(imageUrl);
        this.comodidades = new ArrayList<>();
        this.tipoLocacao = "hora";
        this.timestamp = System.currentTimeMillis();
        this.viewCount = 0;
    }

    @Exclude
    public String getPrecoFormatado() {
        String tipoTexto = "";
        switch (tipoLocacao != null ? tipoLocacao : "hora") {
            case "dia":
                tipoTexto = "/dia";
                break;
            case "mes":
                tipoTexto = "/mês";
                break;
            case "hora":
            default:
                tipoTexto = "/hora";
                break;
        }
        return String.format("R$ %.2f%s", preco, tipoTexto);
    }

    @Exclude
    public String getRatingFormatado() {
        return String.format("%.1f", rating);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nome", nome);
        map.put("endereco", endereco);
        map.put("preco", preco);
        map.put("rating", rating);
        map.put("categoria", categoria);
        map.put("imageUrl", getImageUrl());
        map.put("descricao", descricao);
        map.put("capacidade", capacidade);
        map.put("horarioFuncionamento", horarioFuncionamento);
        map.put("comodidades", comodidades);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("tipoLocacao", tipoLocacao);
        map.put("proprietarioId", proprietarioId);
        map.put("timestamp", timestamp);
        map.put("viewCount", viewCount);
        return map;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public void setHorarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; }

    public List<String> getComodidades() { return comodidades; }
    public void setComodidades(List<String> comodidades) { this.comodidades = comodidades; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getTipoLocacao() { return tipoLocacao; }
    public void setTipoLocacao(String tipoLocacao) { this.tipoLocacao = tipoLocacao; }

    public String getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(String proprietarioId) { this.proprietarioId = proprietarioId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

}
