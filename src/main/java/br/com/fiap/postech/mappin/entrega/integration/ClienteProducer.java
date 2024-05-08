package br.com.fiap.postech.mappin.entrega.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "cliente", url = "${url.cliente}", configuration = FeignConfig.class)
public interface ClienteProducer {
    @GetMapping(value = "/cliente/{idCliente}")
    Cliente obterCliente(@PathVariable UUID idCliente);
}