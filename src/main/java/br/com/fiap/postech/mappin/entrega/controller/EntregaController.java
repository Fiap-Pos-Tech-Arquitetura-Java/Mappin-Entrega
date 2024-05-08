package br.com.fiap.postech.mappin.entrega.controller;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.services.EntregaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/entrega")
public class EntregaController {
    private final EntregaService entregaService;

    @Autowired
    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @Operation(summary = "registra uma entrega para todos os pedidos PAGOS que ainda não foram enviados para entrega")
    @PostMapping
    public ResponseEntity<List<Entrega>> save() {
        List<Entrega> savedEntrega = entregaService.save();
        return new ResponseEntity<>(savedEntrega, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os entregas")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<Entrega>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        Entrega entrega = new Entrega(null, null, status, null);
        entrega.setId(null);
        var pageable = PageRequest.of(page, size);
        var entregas = entregaService.findAll(pageable, entrega);
        return new ResponseEntity<>(entregas, HttpStatus.OK);
    }

    @Operation(summary = "lista um entrega por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            Entrega entrega = entregaService.findById(id);
            return ResponseEntity.ok(entrega);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um entrega por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody Entrega entrega) {
        try {
            Entrega updatedEntrega = entregaService.update(id, entrega);
            return new ResponseEntity<>(updatedEntrega, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um entrega por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            entregaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException
                exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
