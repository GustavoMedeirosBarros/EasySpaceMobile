package com.example.easyspace.models;

public class Categoria {
    private String nome;
    private int iconResId;

    public Categoria() {
    }

    public Categoria(String nome, int iconResId) {
        this.nome = nome;
        this.iconResId = iconResId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}

