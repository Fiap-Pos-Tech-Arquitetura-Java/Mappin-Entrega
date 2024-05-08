package br.com.fiap.postech.mappin.entrega.integration;

import br.com.fiap.postech.mappin.entrega.entities.Pedido;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "pedidos", url = "${url.pedido}", configuration = FeignConfig.class)
public interface PedidoProducer {
    @GetMapping(value = "/pedido/findByStatus/AGUARDANDO_ENTREGA")
    List<Pedido> obterPedidosAguardandoEntrega();
    @PutMapping(value = "/pedido/{idPedido}")
    void atualizaStatus(@PathVariable UUID idPedido, @RequestBody PedidoRequest pedidoRequest);
}
