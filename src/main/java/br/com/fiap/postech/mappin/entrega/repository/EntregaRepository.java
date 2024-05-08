package br.com.fiap.postech.mappin.entrega.repository;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, UUID> {

    Optional<Entrega> findEntregaByStatusAndCepRaiz(String status, String cepRaiz);
    List<Entrega> findEntregaByStatus(String status);
}