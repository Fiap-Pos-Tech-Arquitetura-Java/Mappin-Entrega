package br.com.fiap.postech.mappin.entrega.entities;

import br.com.fiap.postech.mappin.entrega.Generated;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_entrega")
public class Entrega {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "cpf_entregador", nullable = false)
    private String cpfEntregador;

    @Column(name = "cep_raiz", nullable = false)
    private String cepRaiz;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_entrega", nullable = false)
    private List<Pedido> pedidos;

    public Entrega() {
        super();
    }

    public Entrega(String cpfEntregador, String cepRaiz, String status, List<Pedido> pedidos) {
        this.cpfEntregador = cpfEntregador;
        this.cepRaiz = cepRaiz;
        this.status = status;
        this.pedidos = pedidos;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entrega entrega)) return false;
        return Objects.equals(id, entrega.id);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCpfEntregador() {
        return cpfEntregador;
    }

    public void setCpfEntregador(String cpfEntregador) {
        this.cpfEntregador = cpfEntregador;
    }

    public String getCepRaiz() {
        return cepRaiz;
    }

    public void setCepRaiz(String cepRaiz) {
        this.cepRaiz = cepRaiz;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
