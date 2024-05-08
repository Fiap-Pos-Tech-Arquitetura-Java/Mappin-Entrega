package br.com.fiap.postech.mappin.entrega.services;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.enumarations.Status;
import br.com.fiap.postech.mappin.entrega.helper.EntregaHelper;
import br.com.fiap.postech.mappin.entrega.integration.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class EntregaServiceIT {
    @Autowired
    private EntregaService entregaService;
    @MockBean
    private PedidoProducer pedidoProducer;
    @MockBean
    private ClienteProducer clienteProducer;

    @Nested
    class CadastrarEntrega {
        @Test
        void devePermitirCadastrarEntrega_novaEntregaParaUmCepRaiz() {
            // Arrange
            var pedidos = EntregaHelper.getEntrega(false, 1).getPedidos();
            //var pedidoResponse = new PedidoResponse(pedidos);
            when(pedidoProducer.obterPedidosAguardandoEntrega()).thenReturn(pedidos);
            doNothing().when(pedidoProducer).atualizaStatus(any(UUID.class), any(PedidoRequest.class));

            var cepRaiz = "12345";
            var cepCliente = cepRaiz + "000";
            var cliente = new Cliente();
            cliente.setEndereco(new Endereco());
            cliente.getEndereco().setCep(cepCliente);
            when(clienteProducer.obterCliente(any(UUID.class))).thenReturn(cliente);
            // Act
            var entregaSalvo = entregaService.save();
            // Assert
            assertThat(entregaSalvo)
                    .isInstanceOf(List.class)
                    .isNotNull()
                    .hasSize(1);
            assertThat(entregaSalvo.get(0).getCepRaiz()).isEqualTo(cepRaiz);
            assertThat(entregaSalvo.get(0).getStatus()).isEqualTo(Status.PREPARANDO_ENVIO.name());
            assertThat(entregaSalvo.get(0).getCpfEntregador()).isNotNull();
            assertThat(entregaSalvo.get(0).getId()).isNotNull();

            verify(pedidoProducer, times(1)).obterPedidosAguardandoEntrega();
            verify(clienteProducer, times(1)).obterCliente(any(UUID.class));
            verify(pedidoProducer, times(1)).atualizaStatus(any(UUID.class), any(PedidoRequest.class));
        }

        @Test
        void devePermitirCadastrarEntrega_entregaJaExistenteParaUmCepRaiz() {
            // Arrange
            var pedidos = EntregaHelper.getEntrega(false, 1).getPedidos();
            //var pedidoResponse = new PedidoResponse(pedidos);
            when(pedidoProducer.obterPedidosAguardandoEntrega()).thenReturn(pedidos);
            doNothing().when(pedidoProducer).atualizaStatus(any(UUID.class), any(PedidoRequest.class));

            var cepRaiz = "12345";
            var cepCliente = cepRaiz + "000";
            var cliente = new Cliente();
            cliente.setEndereco(new Endereco());
            cliente.getEndereco().setCep(cepCliente);
            when(clienteProducer.obterCliente(any(UUID.class))).thenReturn(cliente);
            // Act
            var entregaSalvo = entregaService.save();
            // Assert
            assertThat(entregaSalvo)
                    .isInstanceOf(List.class)
                    .isNotNull()
                    .hasSize(1);
            assertThat(entregaSalvo.get(0).getCepRaiz()).isEqualTo(cepRaiz);
            assertThat(entregaSalvo.get(0).getStatus()).isEqualTo(Status.PREPARANDO_ENVIO.name());
            assertThat(entregaSalvo.get(0).getCpfEntregador()).isNotNull();
            assertThat(entregaSalvo.get(0).getId()).isNotNull();

            verify(pedidoProducer, times(1)).obterPedidosAguardandoEntrega();
            verify(clienteProducer, times(1)).obterCliente(any(UUID.class));
            verify(pedidoProducer, times(1)).atualizaStatus(any(UUID.class), any(PedidoRequest.class));
        }
    }

    @Nested
    class BuscarEntrega {
        @Test
        void devePermitirBuscarEntregaPorId() {
            // Arrange
            var id = UUID.fromString("e7bdc094-b8b8-4495-b8fb-731f12c24658");
            var cpfEntregador = "63088406027";
            // Act
            var entregaObtido = entregaService.findById(id);
            // Assert
            assertThat(entregaObtido).isNotNull().isInstanceOf(Entrega.class);
            assertThat(entregaObtido.getCpfEntregador()).isEqualTo(cpfEntregador);
            assertThat(entregaObtido.getId()).isNotNull();
            assertThat(entregaObtido.getId()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarEntregaPorId_idNaoExiste() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            UUID uuid = entrega.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> entregaService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + entrega.getId());
        }

        @Test
        void devePermitirBuscarTodosEntrega() {
            // Arrange
            Entrega criteriosDeBusca = new Entrega(null,null,null, null);
            criteriosDeBusca.setId(null);
            // Act
            var listaEntregasObtidos = entregaService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaEntregasObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaEntregasObtidos.getContent()).asList().hasSize(3);
            assertThat(listaEntregasObtidos.getContent()).asList().allSatisfy(
                    entregaObtido -> assertThat(entregaObtido).isNotNull()
            );
        }
    }

    @Nested
    class AlterarEntrega {

        @Test
        void devePermitirAlterarEntrega() {
            // Arrange
            var id = UUID.fromString("70fc4381-5ddd-4181-ae94-8d05dfaa4b69");
            var status = Status.ENTREGUE.name();

            var entrega = new Entrega(null, null, status, null);
            entrega.setId(null);
            // Act
            var entregaAtualizada = entregaService.update(id, entrega);
            // Assert
            assertThat(entregaAtualizada).isNotNull().isInstanceOf(Entrega.class);
            assertThat(entregaAtualizada.getId()).isNotNull();
            assertThat(entregaAtualizada.getStatus()).isEqualTo(status);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_idNaoExiste() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var uuid = entrega.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> entregaService.update(uuid, entrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + entrega.getId());
        }
    }

    @Nested
    class RemoverEntrega {
        @Test
        void devePermitirRemoverEntrega() {
            // Arrange
            var id = UUID.fromString("a0839d7b-a7b4-49c6-be5d-a1c9ff2801b4");
            // Act
            entregaService.delete(id);
            // Assert
            assertThatThrownBy(() -> entregaService.findById(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverEntregaPorId_idNaoExiste() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var uuid = entrega.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> entregaService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + entrega.getId());
        }
    }
}
