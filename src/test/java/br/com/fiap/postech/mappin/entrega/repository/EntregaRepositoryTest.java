package br.com.fiap.postech.mappin.entrega.repository;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.helper.EntregaHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class EntregaRepositoryTest {
    @Mock
    private EntregaRepository entregaRepository;

    AutoCloseable openMocks;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCadastrarEntrega() {
        // Arrange
        var entrega = EntregaHelper.getEntrega(false);
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entrega);
        // Act
        var savedEntrega = entregaRepository.save(entrega);
        // Assert
        assertThat(savedEntrega).isNotNull().isEqualTo(entrega);
        verify(entregaRepository, times(1)).save(any(Entrega.class));
    }

    @Test
    void devePermitirBuscarEntrega() {
        // Arrange
        var entrega = EntregaHelper.getEntrega(true);
        when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
        // Act
        var entregaOpcional = entregaRepository.findById(entrega.getId());
        // Assert
        assertThat(entregaOpcional).isNotNull().containsSame(entrega);
        entregaOpcional.ifPresent(
                entregaRecebido -> {
                    assertThat(entregaRecebido).isInstanceOf(Entrega.class).isNotNull();
                    assertThat(entregaRecebido.getId()).isEqualTo(entrega.getId());
                    assertThat(entregaRecebido.getCpfEntregador()).isEqualTo(entrega.getCpfEntregador());
                    assertThat(entregaRecebido.getCepRaiz()).isEqualTo(entrega.getCepRaiz());
                    assertThat(entregaRecebido.getStatus()).isEqualTo(entrega.getStatus());
                    assertThat(entregaRecebido.getPedidos()).isNotNull();
                    assertThat(entregaRecebido.getPedidos().get(0)).isEqualTo(entrega.getPedidos().get(0));
                    assertThat(entregaRecebido.getPedidos().get(0).getId()).isEqualTo(entrega.getPedidos().get(0).getId());
                    assertThat(entregaRecebido.getPedidos().get(1)).isEqualTo(entrega.getPedidos().get(1));
                    assertThat(entregaRecebido.getPedidos().get(1).getId()).isEqualTo(entrega.getPedidos().get(1).getId());
                }
        );
        verify(entregaRepository, times(1)).findById(entrega.getId());
    }
    @Test
    void devePermitirRemoverEntrega() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(entregaRepository).deleteById(id);
        //Act
        entregaRepository.deleteById(id);
        //Assert
        verify(entregaRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarEntregas() {
        // Arrange
        var entrega1 = EntregaHelper.getEntrega(true);
        var entrega2 = EntregaHelper.getEntrega(true);
        var listaEntregas = Arrays.asList(
                entrega1,
                entrega2
        );
        when(entregaRepository.findAll()).thenReturn(listaEntregas);
        // Act
        var entregasListados = entregaRepository.findAll();
        assertThat(entregasListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(entrega1, entrega2);
        verify(entregaRepository, times(1)).findAll();
    }
}