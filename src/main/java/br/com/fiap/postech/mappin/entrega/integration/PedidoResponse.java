package br.com.fiap.postech.mappin.entrega.integration;

import br.com.fiap.postech.mappin.entrega.entities.Pedido;

import java.util.List;

public record PedidoResponse(List<Pedido> pedidos) {
}
