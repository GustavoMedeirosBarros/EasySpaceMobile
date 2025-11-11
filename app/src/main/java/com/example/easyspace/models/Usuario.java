package com.example.easyspace.models;

import java.io.Serializable;
import java.util.ArrayList; // Adicionado
import java.util.HashMap;
import java.util.List; // Adicionado
import java.util.Map;

public class Usuario implements Serializable {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String documento;
    private String cep;
    private String cidade;
    private String estado;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String dataNascimento;
    private String genero;
    private String fotoUrl;
    private boolean isPessoaFisica;
    private long dataCriacao;

    private boolean profileComplete;
    private String cpf;
    private String rua;
    private List<String> favoritos;


    public Usuario() {
        this.dataCriacao = System.currentTimeMillis();
        this.favoritos = new ArrayList<>();
    }

    public Usuario(String id, String nome, String email, String telefone, String documento,
                   String cep, String endereco, String numero, String complemento, String bairro,
                   String cidade, String estado, String dataNascimento, String genero,
                   boolean isPessoaFisica) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.documento = documento;
        this.cep = cep;
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.isPessoaFisica = isPessoaFisica;
        this.dataCriacao = System.currentTimeMillis();
        this.favoritos = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nome", nome);
        map.put("email", email);
        map.put("telefone", telefone);
        map.put("documento", documento);
        map.put("cep", cep);
        map.put("endereco", endereco);
        map.put("numero", numero);
        map.put("complemento", complemento);
        map.put("bairro", bairro);
        map.put("cidade", cidade);
        map.put("estado", estado);
        map.put("dataNascimento", dataNascimento);
        map.put("genero", genero);
        map.put("fotoUrl", fotoUrl);
        map.put("isPessoaFisica", isPessoaFisica);

        map.put("profileComplete", profileComplete);
        map.put("cpf", cpf);
        map.put("rua", rua);
        map.put("favoritos", favoritos);

        return map;
    }



    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public boolean isPessoaFisica() { return isPessoaFisica; }
    public void setPessoaFisica(boolean pessoaFisica) { this.isPessoaFisica = pessoaFisica; }
    public long getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(long dataCriacao) { this.dataCriacao = dataCriacao; }


    public boolean isProfileComplete() { return profileComplete; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public List<String> getFavoritos() { return favoritos; }
    public void setFavoritos(List<String> favoritos) { this.favoritos = favoritos; }


    public String getIniciais() {
        if (nome != null && !nome.isEmpty()) {
            String[] partes = nome.split(" ");
            if (partes.length > 1) {
                return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
            }
            return String.valueOf(nome.charAt(0)).toUpperCase();
        }
        return "?";
    }

    public String getDocumentoFormatado() {
        return documento;
    }

    public String getTelefoneFormatado() {
        return telefone;
    }

    public String getCepFormatado() {
        return cep;
    }

    public String getEnderecoCompleto() {
        return endereco;
    }
}