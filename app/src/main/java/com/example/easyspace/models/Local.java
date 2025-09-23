package com.example.easyspace.models;

public class Local {
    private String nome;
    private String descricao;
    private double preco;
    private String localizacao;
    private float rating;
    private String imagemUrl;

    public Local(String nome, String descricao, double preco, String localizacao, float rating, String imagemUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.localizacao = localizacao;
        this.rating = rating;
        this.imagemUrl = imagemUrl;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public float getRating() {
        return rating;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}
