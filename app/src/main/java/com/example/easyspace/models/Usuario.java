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
    private String documento; // Para CPF ou CNPJ
    private String cep;
    private String cidade;
    private String estado;
    private String endereco; // Usado para Endereço Completo ou Rua
    private String numero;
    private String complemento;
    private String bairro;
    private String dataNascimento;
    private String genero;
    private String fotoUrl;
    private boolean isPessoaFisica;
    private long dataCriacao;

    // --- CAMPOS ADICIONADOS PARA CORRIGIR ERROS DO LOGCAT ---
    private boolean profileComplete; // Para `CompleteProfileActivity`
    private String cpf; // `CompleteProfileActivity` estava salvando 'cpf'
    private String rua; // `CompleteProfileActivity` estava salvando 'rua'
    private List<String> favoritos; // Para a lista de favoritos

    public Usuario() {
        this.dataCriacao = System.currentTimeMillis();
        this.favoritos = new ArrayList<>(); // Inicializa a lista
    }

    public Usuario(String id, String nome, String email, String telefone, String documento,
                   String cep, String endereco, String numero, String complemento, String bairro,
                   String cidade, String estado, String dataNascimento, String genero,
                   boolean isPessoaFisica) {
        // ... (construtor existente)
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

        // Adicionados
        map.put("profileComplete", profileComplete);
        map.put("cpf", cpf);
        map.put("rua", rua);
        map.put("favoritos", favoritos);

        return map;
    }

    // --- Getters e Setters ---
    // (Getters e Setters existentes para id, nome, email, etc.)

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

    // --- Getters e Setters ADICIONADOS ---

    public boolean isProfileComplete() { return profileComplete; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public List<String> getFavoritos() { return favoritos; }
    public void setFavoritos(List<String> favoritos) { this.favoritos = favoritos; }

    // --- Getters Formatados (Opcionais, mas bons de manter) ---
    // ... (getIniciais, getDocumentoFormatado, etc.) ...
    // (O Firestore estava avisando sobre eles, mas eles não causam erro,
    // apenas não são usados para salvar dados)

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
        // ...
        return documento; // Simplificado para exemplo
    }

    public String getTelefoneFormatado() {
        // ...
        return telefone; // Simplificado para exemplo
    }

    public String getCepFormatado() {
        // ...
        return cep; // Simplificado para exemplo
    }

    public String getEnderecoCompleto() {
        // ...
        return endereco; // Simplificado para exemplo
    }
}