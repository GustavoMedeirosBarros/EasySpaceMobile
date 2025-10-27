package com.example.easyspace.models;


import java.io.Serializable;
import java.util.HashMap;
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

    public Usuario() {
        this.dataCriacao = System.currentTimeMillis();
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
        return map;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getDocumento() {
        return documento;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public boolean isPessoaFisica() {
        return isPessoaFisica;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setPessoaFisica(boolean pessoaFisica) {
        isPessoaFisica = pessoaFisica;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

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
        if (documento == null || documento.isEmpty()) {
            return "";
        }

        String numeroLimpo = documento.replaceAll("[^0-9]", "");

        if (isPessoaFisica && numeroLimpo.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    numeroLimpo.substring(0, 3),
                    numeroLimpo.substring(3, 6),
                    numeroLimpo.substring(6, 9),
                    numeroLimpo.substring(9, 11));
        } else if (!isPessoaFisica && numeroLimpo.length() == 14) {
            return String.format("%s.%s.%s/%s-%s",
                    numeroLimpo.substring(0, 2),
                    numeroLimpo.substring(2, 5),
                    numeroLimpo.substring(5, 8),
                    numeroLimpo.substring(8, 12),
                    numeroLimpo.substring(12, 14));
        }

        return documento;
    }

    public String getTelefoneFormatado() {
        if (telefone == null || telefone.isEmpty()) {
            return "";
        }

        String numeroLimpo = telefone.replaceAll("[^0-9]", "");

        if (numeroLimpo.length() == 11) {
            return String.format("(%s) %s-%s",
                    numeroLimpo.substring(0, 2),
                    numeroLimpo.substring(2, 7),
                    numeroLimpo.substring(7, 11));
        } else if (numeroLimpo.length() == 10) {
            return String.format("(%s) %s-%s",
                    numeroLimpo.substring(0, 2),
                    numeroLimpo.substring(2, 6),
                    numeroLimpo.substring(6, 10));
        }

        return telefone;
    }

    public String getCepFormatado() {
        if (cep == null || cep.isEmpty()) {
            return "";
        }

        String numeroLimpo = cep.replaceAll("[^0-9]", "");

        if (numeroLimpo.length() == 8) {
            return String.format("%s-%s",
                    numeroLimpo.substring(0, 5),
                    numeroLimpo.substring(5, 8));
        }

        return cep;
    }

    public String getEnderecoCompleto() {
        StringBuilder enderecoCompleto = new StringBuilder();

        if (endereco != null && !endereco.isEmpty()) {
            enderecoCompleto.append(endereco);
        }

        if (numero != null && !numero.isEmpty()) {
            if (enderecoCompleto.length() > 0) {
                enderecoCompleto.append(", ");
            }
            enderecoCompleto.append(numero);
        }

        if (complemento != null && !complemento.isEmpty()) {
            if (enderecoCompleto.length() > 0) {
                enderecoCompleto.append(", ");
            }
            enderecoCompleto.append(complemento);
        }

        if (bairro != null && !bairro.isEmpty()) {
            if (enderecoCompleto.length() > 0) {
                enderecoCompleto.append(" - ");
            }
            enderecoCompleto.append(bairro);
        }

        if (cidade != null && !cidade.isEmpty()) {
            if (enderecoCompleto.length() > 0) {
                enderecoCompleto.append(", ");
            }
            enderecoCompleto.append(cidade);
        }

        if (estado != null && !estado.isEmpty()) {
            if (enderecoCompleto.length() > 0) {
                enderecoCompleto.append(" - ");
            }
            enderecoCompleto.append(estado);
        }

        return enderecoCompleto.toString();
    }
}
