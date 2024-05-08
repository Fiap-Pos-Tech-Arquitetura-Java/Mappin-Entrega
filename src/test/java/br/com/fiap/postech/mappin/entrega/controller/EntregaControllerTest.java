package br.com.fiap.postech.mappin.entrega.controller;

import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.helper.EntregaHelper;
import br.com.fiap.postech.mappin.entrega.services.EntregaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EntregaControllerTest {
    public static final String CLIENTE = "/entrega";
    private MockMvc mockMvc;
    @Mock
    private EntregaService entregaService;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        EntregaController entregaController = new EntregaController(entregaService);
        mockMvc = MockMvcBuilders.standaloneSetup(entregaController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarEntrega {
        @Test
        void devePermitirCadastrarEntrega() throws Exception {
            // Arrange
            var listaEntregas = List.of(EntregaHelper.getEntrega(true));
            when(entregaService.save()).thenReturn(listaEntregas);
            // Act
            mockMvc.perform(
                            post(CLIENTE).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
            // Assert
            verify(entregaService, times(1)).save();
        }
    }
    @Nested
    class BuscarEntrega {
        @Test
        void devePermitirBuscarEntregaPorId() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaService.findById(any(UUID.class))).thenReturn(entrega);
            // Act
            mockMvc.perform(get("/entrega/{id}", entrega.getId().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(entregaService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarEntregaPorId_idNaoExiste() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaService.findById(entrega.getId())).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(get("/entrega/{id}", entrega.getId().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(entregaService, times(1)).findById(entrega.getId());
        }

        @Test
        void devePermitirBuscarTodosEntrega() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var entrega = EntregaHelper.getEntrega(true);
            var criterioEntrega = new Entrega(null, null, entrega.getStatus(), null);
            criterioEntrega.setId(null);
            List<Entrega> listEntrega = new ArrayList<>();
            listEntrega.add(entrega);
            Page<Entrega> entregas = new PageImpl<>(listEntrega);
            var pageable = PageRequest.of(page, size);
            when(entregaService.findAll(
                            pageable,
                            criterioEntrega
                    )
            ).thenReturn(entregas);
            // Act
            mockMvc.perform(
                            get("/entrega")
                                    .param("page", String.valueOf(page))
                                    .param("size", String.valueOf(size))
                                    .param("status", entrega.getStatus())
                    )
                    //.andDo(print())
                    .andExpect(status().is5xxServerError())
            //.andExpect(jsonPath("$.content", not(empty())))
            //.andExpect(jsonPath("$.totalPages").value(1))
            //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(entregaService, times(1)).findAll(pageable, criterioEntrega);
        }
    }

    @Nested
    class AlterarEntrega {
        @Test
        void devePermitirAlterarEntrega() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaService.update(entrega.getId(), entrega)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/entrega/{id}", entrega.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(entrega)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(entregaService, times(1)).update(entrega.getId(), entrega);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntrega_RequisicaoXml() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaService.update(entrega.getId(), entrega)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/entrega/{id}", entrega.getId())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(entrega)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(entregaService, never()).update(entrega.getId(), entrega);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarEntregaPorId_idNaoExiste() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            when(entregaService.update(entrega.getId(), entrega)).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(put("/entrega/{id}", entrega.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(entrega)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(entregaService, times(1)).update(any(UUID.class), any(Entrega.class));
        }
    }

    @Nested
    class RemoverEntrega {
        @Test
        void devePermitirRemoverEntrega() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            doNothing().when(entregaService).delete(entrega.getId());
            // Act
            mockMvc.perform(delete("/entrega/{id}", entrega.getId()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(entregaService, times(1)).delete(entrega.getId());
            verify(entregaService, times(1)).delete(entrega.getId());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverEntregaPorId_idNaoExiste() throws Exception {
            // Arrange
            var entrega = EntregaHelper.getEntrega(true);
            doThrow(new IllegalArgumentException("Entrega não encontrado com o ID: " + entrega.getId()))
                    .when(entregaService).delete(entrega.getId());
            // Act
            mockMvc.perform(delete("/entrega/{id}", entrega.getId()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(entregaService, times(1)).delete(entrega.getId());
        }
    }
}