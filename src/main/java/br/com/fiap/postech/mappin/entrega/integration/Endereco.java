package br.com.fiap.postech.mappin.entrega.integration;

public class Endereco {
    private String cep;

    public Endereco() {
        super();
    }

    public Endereco(String cep) {
        this.cep = cep;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
