package br.com.fiap.postech.mappin.entrega.controller;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.entities.Pedido;
import br.com.fiap.postech.mappin.entrega.enumarations.Status;
import br.com.fiap.postech.mappin.entrega.helper.EntregaHelper;
import br.com.fiap.postech.mappin.entrega.integration.*;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class EntregaControllerIT {

    public static final String PATH = "/mappin/entrega";
    @LocalServerPort
    private int port;
    @MockBean
    private PedidoProducer pedidoProducer;
    @MockBean
    private ClienteProducer clienteProducer;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarEntrega {
        @Test
        void devePermitirCadastrarEntrega() {
            var listaEntregas = List.of(EntregaHelper.getEntrega(false));

            List<Pedido> pedidos = EntregaHelper.getEntrega(true).getPedidos();
            //var pedidoResponse = new PedidoResponse(pedidos);
            when(pedidoProducer.obterPedidosAguardandoEntrega()).thenReturn(pedidos);
            var cliente = new Cliente();
            cliente.setEndereco(new Endereco("95735970"));
            when(clienteProducer.obterCliente(any(UUID.class))).thenReturn(cliente);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(listaEntregas)
            .when()
                .post(PATH)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/entrega.list.schema.json"));
        }

/*        @Test
        void deveGerarExcecao_QuandoCadastrarEntrega_comClienteInexistente() {
            var entrega = EntregaHelper.getEntrega(false);
            doThrow(new IllegalArgumentException("Cliente não encontrado com o ID: " + entrega.get)).when(clienteProducer).clienteExiste(any(UUID.class));
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post(PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }*/
    }

    @Nested
    class BuscarEntrega {
        @Test
        void devePermitirBuscarEntregaPorId() {
            var id = "70fc4381-5ddd-4181-ae94-8d05dfaa4b69";
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH + "/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/entrega.schema.json"));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarEntregaPorId_idNaoExiste() {
            var id = EntregaHelper.getEntrega(true).getId();
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH + "/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosEntrega() {
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/entrega.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosEntrega_ComPaginacao() {
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/entrega.page.schema.json"));
        }
    }

    @Nested
    class AlterarEntrega {
        @Test
        void devePermitirAlterarEntrega() {
            var pedido = new Pedido(UUID.fromString("5312498e-25ed-4946-9a9a-809011d41053"));
            pedido.setId(UUID.fromString("5312498e-25ed-4946-9a9a-809011d41053"));
            var pedidos = List.of(pedido);
            var entrega = new Entrega(
                    "86655281059",
                    "95870",
                    Status.ENTREGUE.name(),
                    pedidos
            );
            entrega.setId(UUID.fromString("a0839d7b-a7b4-49c6-be5d-a1c9ff2801b4"));
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .body(entrega).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put(PATH + "/{id}", entrega.getId())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/entrega.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntrega_RequisicaoXml() {
            var entrega = EntregaHelper.getEntrega(true);
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .body(entrega).contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .put(PATH + "/{id}", entrega.getId())
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_idNaoExiste() {
            var entrega = EntregaHelper.getEntrega(true);
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
                .body(entrega).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put(PATH + "/{id}", entrega.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Entrega não encontrado com o ID: " + entrega.getId()));
        }
    }

    @Nested
    class RemoverEntrega {
        @Test
        void devePermitirRemoverEntrega() {
            var entrega = new Entrega(
                    RandomStringUtils.random(5),
                    RandomStringUtils.random(5),
                    Status.ENTREGUE.name(),
                    null
            );
            entrega.setId(UUID.fromString("e7bdc094-b8b8-4495-b8fb-731f12c24658"));
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
            .when()
                .delete(PATH + "/{id}", entrega.getId())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverEntregaPorId_idNaoExiste() {
            var entrega = EntregaHelper.getEntrega(true);
            given()
                //.header(HttpHeaders.AUTHORIZATION, EntregaHelper.getToken())
            .when()
                .delete(PATH + "/{id}", entrega.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Entrega não encontrado com o ID: " + entrega.getId()));
        }
    }
}
