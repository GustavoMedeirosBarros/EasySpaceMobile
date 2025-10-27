package com.example.easyspace.models;

import java.io.Serializable;

public class Comentario implements Serializable {
    private String id;
    private String localId;
    private String usuarioId;
    private String usuarioNome;
    private String usuarioFotoUrl;
    private String texto;
    private double rating;
    private long timestamp;
    private boolean verificadoLocacao;

    public Comentario() {
        this.timestamp = System.currentTimeMillis();
    }

    public Comentario(String localId, String usuarioId, String usuarioNome, String texto, double rating) {
        this.localId = localId;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.texto = texto;
        this.rating = rating;
        this.timestamp = System.currentTimeMillis();
        this.verificadoLocacao = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }

    public String getUsuarioFotoUrl() { return usuarioFotoUrl; }
    public void setUsuarioFotoUrl(String usuarioFotoUrl) { this.usuarioFotoUrl = usuarioFotoUrl; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isVerificadoLocacao() { return verificadoLocacao; }
    public void setVerificadoLocacao(boolean verificadoLocacao) { this.verificadoLocacao = verificadoLocacao; }
}
