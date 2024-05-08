package br.com.fiap.postech.mappin.entrega.repository;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.enumarations.Status;
import br.com.fiap.postech.mappin.entrega.helper.EntregaHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class EntregaRepositoryIT {
    @Autowired
    private EntregaRepository entregaRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = entregaRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }

    @Test
    void devePermitirCadastrarEntrega() {
        // Arrange
        var entrega = EntregaHelper.getEntrega(true);
        //entrega.setStatus(Status.AGUARDANDO_PAGAMENTO.name());
        //entrega.setValorTotal(1d);
        // Act
        var entregaCadastrado = entregaRepository.save(entrega);
        // Assert
        assertThat(entregaCadastrado).isInstanceOf(Entrega.class).isNotNull();
        assertThat(entregaCadastrado.getId()).isEqualTo(entrega.getId());
        assertThat(entregaCadastrado.getCpfEntregador()).isEqualTo(entrega.getCpfEntregador());
        assertThat(entregaCadastrado.getCepRaiz()).isEqualTo(entrega.getCepRaiz());
        assertThat(entregaCadastrado.getStatus()).isEqualTo(entrega.getStatus());
        assertThat(entregaCadastrado.getPedidos()).isNotNull();
        assertThat(entregaCadastrado.getPedidos().get(0)).isEqualTo(entrega.getPedidos().get(0));
        assertThat(entregaCadastrado.getPedidos().get(0).getId()).isEqualTo(entrega.getPedidos().get(0).getId());
        assertThat(entregaCadastrado.getPedidos().get(1)).isEqualTo(entrega.getPedidos().get(1));
        assertThat(entregaCadastrado.getPedidos().get(1).getId()).isEqualTo(entrega.getPedidos().get(1).getId());
    }
    @Test
    void devePermitirBuscarEntrega() {
        // Arrange
        var id = UUID.fromString("70fc4381-5ddd-4181-ae94-8d05dfaa4b69");
        var status = Status.ENTREGUE.name();
        // Act
        var entregaOpcional = entregaRepository.findById(id);
        // Assert
        assertThat(entregaOpcional).isPresent();
        entregaOpcional.ifPresent(
                entregaRecebida -> {
                    assertThat(entregaRecebida).isInstanceOf(Entrega.class).isNotNull();
                    assertThat(entregaRecebida.getId()).isEqualTo(id);
                    assertThat(entregaRecebida.getStatus()).isEqualTo(status);
                }
        );
    }
    @Test
    void devePermitirRemoverEntrega() {
        // Arrange
        var id = UUID.fromString("8855e7b2-77b6-448b-97f8-8a0b529f3976");
        // Act
        entregaRepository.deleteById(id);
        // Assert
        var entregaOpcional = entregaRepository.findById(id);
        assertThat(entregaOpcional).isEmpty();
    }
    @Test
    void devePermitirListarEntregas() {
        // Arrange
        // Act
        var entregasListados = entregaRepository.findAll();
        // Assert
        assertThat(entregasListados).hasSize(3);
    }
}
