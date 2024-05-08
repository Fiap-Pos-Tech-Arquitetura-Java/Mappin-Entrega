package br.com.fiap.postech.mappin.entrega.helper;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.entities.Pedido;

import java.util.ArrayList;
import java.util.UUID;

public class EntregaHelper {

    public static Entrega getEntrega(boolean geraId) {
        return getEntrega(geraId, 3);
    }
    public static Entrega getEntrega(boolean geraId, Integer quantidadePedidos) {
        var pedidos = new ArrayList<Pedido>();
        for (int i=0 ; i < quantidadePedidos ; i++) {
            var pedido = new Pedido(UUID.randomUUID());
            pedido.setId(UUID.randomUUID());
            pedido.setIdCliente(UUID.randomUUID());
            pedidos.add(pedido);
        }
        var entrega = new Entrega(
                "47978839010",
                "95880",
                "AGUARDANDO_ENTREGA",
                pedidos
        );
        if (geraId) {
            entrega.setId(UUID.randomUUID());
        }
        return entrega;
    }
}
