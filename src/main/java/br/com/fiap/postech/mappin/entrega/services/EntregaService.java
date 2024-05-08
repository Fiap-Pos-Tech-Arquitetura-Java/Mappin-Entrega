package br.com.fiap.postech.mappin.entrega.services;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EntregaService {
    List<Entrega> save();

    Page<Entrega> findAll(Pageable pageable, Entrega entrega);

    Entrega findById(UUID id);

    Entrega update(UUID id, Entrega entrega);

    void delete(UUID id);
}
