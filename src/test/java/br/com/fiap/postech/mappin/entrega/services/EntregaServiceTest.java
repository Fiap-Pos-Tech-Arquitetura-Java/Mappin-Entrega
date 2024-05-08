package br.com.fiap.postech.mappin.entrega.services;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.enumarations.Status;
import br.com.fiap.postech.mappin.entrega.helper.EntregaHelper;
import br.com.fiap.postech.mappin.entrega.integration.*;
import br.com.fiap.postech.mappin.entrega.repository.EntregaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EntregaServiceTest {
    private EntregaService entregaService;

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private PedidoProducer pedidoProducer;

    @Mock
    private ClienteProducer clienteProducer;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        entregaService = new EntregaServiceImpl(entregaRepository, pedidoProducer, clienteProducer);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class GerarEntrega {
        @Test
        void devePermitirCadastrarEntrega_novaEntregaParaUmCepRaiz() throws Exception {
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

            when(entregaRepository.findEntregaByStatusAndCepRaiz(anyString(), anyString())).thenReturn(Optional.empty());

            var entrega = new Entrega("38123352042", cepRaiz, Status.AGUARDANDO_ENTREGA.name(), pedidos);
            entrega.setId(UUID.randomUUID());
            when(entregaRepository.findEntregaByStatus(anyString())).thenReturn(List.of(entrega));

            when(entregaRepository.save(any(Entrega.class))).thenAnswer(r -> r.getArgument(0));
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

            verify(entregaRepository, times(2)).save(any(Entrega.class));
            verify(entregaRepository, times(2)).findEntregaByStatus(anyString());
            verify(entregaRepository, times(1)).findEntregaByStatusAndCepRaiz(anyString(), anyString());
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

            var entrega = EntregaHelper.getEntrega(false, 1);
            var cepRaiz = entrega.getCepRaiz();
            var cepCliente = cepRaiz + "000";
            var cliente = new Cliente();
            cliente.setEndereco(new Endereco());
            cliente.getEndereco().setCep(cepCliente);
            when(clienteProducer.obterCliente(any(UUID.class))).thenReturn(cliente);

            when(entregaRepository.findEntregaByStatusAndCepRaiz(anyString(), anyString())).thenReturn(Optional.of(entrega));

            //var entrega = new Entrega("38123352042", cepRaiz, Status.AGUARDANDO_ENTREGA.name(), pedidos);
            entrega.setId(UUID.randomUUID());
            when(entregaRepository.findEntregaByStatus(anyString())).thenReturn(List.of(entrega));

            when(entregaRepository.save(any(Entrega.class))).thenAnswer(r -> r.getArgument(0));
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

            verify(entregaRepository, times(2)).save(any(Entrega.class));
            verify(entregaRepository, times(2)).findEntregaByStatus(anyString());
            verify(entregaRepository, times(1)).findEntregaByStatusAndCepRaiz(anyString(), anyString());
            verify(pedidoProducer, times(1)).obterPedidosAguardandoEntrega();
            verify(clienteProducer, times(1)).obterCliente(any(UUID.class));
            verify(pedidoProducer, times(2)).atualizaStatus(any(UUID.class), any(PedidoRequest.class));
        }
    }

    @Nested
    class BuscarEntrega {
        @Test
        void devePermitirBuscarEntregaPorId() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            // Act
            var entregaObtido = entregaService.findById(entrega.getId());
            // Assert
            assertThat(entregaObtido).isEqualTo(entrega);
            verify(entregaRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarEntregaPorId_idNaoExiste() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.empty());
            UUID uuid = entrega.getId();
            // Act
            assertThatThrownBy(() -> entregaService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + entrega.getId());
            // Assert
            verify(entregaRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosEntrega() {
            // Arrange
            Entrega criteriosDeBusca = EntregaHelper.getEntrega(false);
            Page<Entrega> entregas = new PageImpl<>(Arrays.asList(
                    EntregaHelper.getEntrega(true),
                    EntregaHelper.getEntrega(true),
                    EntregaHelper.getEntrega(true)
            ));
            when(entregaRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(entregas);
            // Act
            var entregasObtidos = entregaService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(entregasObtidos).hasSize(3);
            assertThat(entregasObtidos.getContent()).asList().allSatisfy(
                    entrega -> assertThat(entrega)
                            .isNotNull()
                            .isInstanceOf(Entrega.class)
            );
            verify(entregaRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarEntrega {
        @Test
        void devePermitirAlterarEntrega() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var entregaReferencia = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    entrega.getStatus(),
                    entrega.getPedidos()
            );
            var novoEntrega = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    Status.PREPARANDO_ENVIO.name(),
                    entrega.getPedidos()
            );
            novoEntrega.setId(entrega.getId());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            when(entregaRepository.save(any(Entrega.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var entregaSalvo = entregaService.update(entrega.getId(), novoEntrega);
            // Assert
            assertThat(entregaSalvo)
                    .isInstanceOf(Entrega.class)
                    .isNotNull();

            assertThat(entregaSalvo.getStatus()).isEqualTo(novoEntrega.getStatus());
            assertThat(entregaSalvo.getStatus()).isNotEqualTo(entregaReferencia.getStatus());

            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, times(1)).save(any(Entrega.class));
        }
        @Test
        void devePermitirAlterarEntrega_semBody() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var novoEntrega = new Entrega(null, null, null, null);
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            when(entregaRepository.save(any(Entrega.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var entregaSalvo = entregaService.update(entrega.getId(), novoEntrega);
            // Assert
            assertThat(entregaSalvo)
                    .isInstanceOf(Entrega.class)
                    .isNotNull();

            assertThat(entregaSalvo.getStatus()).isEqualTo(entrega.getStatus());

            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, times(1)).save(any(Entrega.class));
        }

        @Test
        void devePermitirAlterarEntrega_statusPagamentoRealizado() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var entregaReferencia = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    entrega.getStatus(),
                    entrega.getPedidos()
            );
            var novoEntrega = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    Status.ENTREGUE.name(),
                    entrega.getPedidos()
            );
            novoEntrega.setId(entrega.getId());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            when(entregaRepository.save(any(Entrega.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var entregaSalvo = entregaService.update(entrega.getId(), novoEntrega);
            // Assert
            assertThat(entregaSalvo)
                    .isInstanceOf(Entrega.class)
                    .isNotNull();

            assertThat(entregaSalvo.getStatus()).isEqualTo(novoEntrega.getStatus());
            assertThat(entregaSalvo.getStatus()).isNotEqualTo(entregaReferencia.getStatus());

            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, times(1)).save(any(Entrega.class));
            //verify(produtoProducer, times(2)).removerDoEstoque(any(ProdutoRequest.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_alterandoId() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var novoEntrega = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    Status.AGUARDANDO_ENTREGA.name(),
                    entrega.getPedidos()
            );
            novoEntrega.setId(UUID.randomUUID());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            // Act && Assert
            assertThatThrownBy(() -> entregaService.update(entrega.getId(), novoEntrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o id de um entrega.");
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).save(any(Entrega.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_alterandoEntregador() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var novoEntrega = new Entrega(
                    "20110055071",
                    entrega.getCepRaiz(),
                    Status.AGUARDANDO_ENTREGA.name(),
                    entrega.getPedidos()
            );
            novoEntrega.setId(entrega.getId());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            // Act && Assert
            assertThatThrownBy(() -> entregaService.update(entrega.getId(), novoEntrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o entregador de um entrega.");
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).save(any(Entrega.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_alterandoCepRaiz() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var novoEntrega = new Entrega(
                    entrega.getCpfEntregador(),
                    "95780",
                    Status.AGUARDANDO_ENTREGA.name(),
                    entrega.getPedidos()
            );
            novoEntrega.setId(entrega.getId());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            // Act && Assert
            assertThatThrownBy(() -> entregaService.update(entrega.getId(), novoEntrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o cep raiz de um entrega.");
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).save(any(Entrega.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_alterandoStatusDesconhecido() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var novoEntrega = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    Status.AGUARDANDO_ENTREGA.name() + "X",
                    entrega.getPedidos()
            );
            novoEntrega.setId(entrega.getId());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            // Act && Assert
            assertThatThrownBy(() -> entregaService.update(entrega.getId(), novoEntrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Status " + novoEntrega.getStatus() + " não existe");
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).save(any(Entrega.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_alterandoItens() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            var entregaItens = EntregaHelper.getEntrega(true);
            var novoEntrega = new Entrega(
                    entrega.getCpfEntregador(),
                    entrega.getCepRaiz(),
                    Status.AGUARDANDO_ENTREGA.name(),
                    entregaItens.getPedidos()
            );
            novoEntrega.setId(entrega.getId());
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            // Act && Assert
            assertThatThrownBy(() -> entregaService.update(entrega.getId(), novoEntrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar os pedidos um entrega.");
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).save(any(Entrega.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_idNaoExiste() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.empty());
            UUID uuid = entrega.getId();
            // Act && Assert
            assertThatThrownBy(() -> entregaService.update(uuid, entrega))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + entrega.getId());
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).save(any(Entrega.class));
        }
    }

    @Nested
    class RemoverEntrega {
        @Test
        void devePermitirRemoverEntrega() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaRepository.findById(entrega.getId())).thenReturn(Optional.of(entrega));
            doNothing().when(entregaRepository).deleteById(entrega.getId());
            // Act
            entregaService.delete(entrega.getId());
            // Assert
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverEntregaPorId_idNaoExiste() {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            doNothing().when(entregaRepository).deleteById(entrega.getId());
            UUID uuid = entrega.getId();
            // Act && Assert
            assertThatThrownBy(() -> entregaService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entrega não encontrado com o ID: " + entrega.getId());
            verify(entregaRepository, times(1)).findById(any(UUID.class));
            verify(entregaRepository, never()).deleteById(any(UUID.class));
        }
    }
}