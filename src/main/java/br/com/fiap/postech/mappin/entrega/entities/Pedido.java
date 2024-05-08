package br.com.fiap.postech.mappin.entrega.entities;

import br.com.fiap.postech.mappin.entrega.Generated;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_pedido")
public class Pedido {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Transient
    private UUID idCliente;

    public Pedido() {
        super();
    }

    public Pedido(UUID id) {
        this();
        this.id = id;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido pedido)) return false;
        return Objects.equals(id, pedido.id);
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

    public UUID getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(UUID idCliente) {
        this.idCliente = idCliente;
    }
}
