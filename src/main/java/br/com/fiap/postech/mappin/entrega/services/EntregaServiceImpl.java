package br.com.fiap.postech.mappin.entrega.services;

import br.com.fiap.postech.mappin.entrega.Generated;
import br.com.fiap.postech.mappin.entrega.entities.Entrega;
import br.com.fiap.postech.mappin.entrega.enumarations.Status;
import br.com.fiap.postech.mappin.entrega.integration.Cliente;
import br.com.fiap.postech.mappin.entrega.integration.ClienteProducer;
import br.com.fiap.postech.mappin.entrega.integration.PedidoProducer;
import br.com.fiap.postech.mappin.entrega.integration.PedidoRequest;
import br.com.fiap.postech.mappin.entrega.repository.EntregaRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntregaServiceImpl implements EntregaService {
    
    private final EntregaRepository entregaRepository;
    private final PedidoProducer pedidoProducer;
    private final ClienteProducer clienteProducer;

    @Autowired
    public EntregaServiceImpl(EntregaRepository entregaRepository, PedidoProducer pedidoProducer,
                              ClienteProducer clienteProducer) {
        this.entregaRepository = entregaRepository;
        this.pedidoProducer = pedidoProducer;
        this.clienteProducer = clienteProducer;
    }

    @Generated
    private static class GeraCpfCnpj {

        private static int randomiza(int n) {
            int ranNum = (int) (Math.random() * n);
            return ranNum;
        }

        private static int mod(int dividendo, int divisor) {
            return (int) Math.round(dividendo - (Math.floor(dividendo / divisor) * divisor));
        }

        public static String cpf() {
            int n = 9;
            int n1 = randomiza(n);
            int n2 = randomiza(n);
            int n3 = randomiza(n);
            int n4 = randomiza(n);
            int n5 = randomiza(n);
            int n6 = randomiza(n);
            int n7 = randomiza(n);
            int n8 = randomiza(n);
            int n9 = randomiza(n);
            int d1 = n9 * 2 + n8 * 3 + n7 * 4 + n6 * 5 + n5 * 6 + n4 * 7 + n3 * 8 + n2 * 9 + n1 * 10;

            d1 = 11 - (mod(d1, 11));

            if (d1 >= 10)
                d1 = 0;

            int d2 = d1 * 2 + n9 * 3 + n8 * 4 + n7 * 5 + n6 * 6 + n5 * 7 + n4 * 8 + n3 * 9 + n2 * 10 + n1 * 11;

            d2 = 11 - (mod(d2, 11));

            String retorno = null;

            if (d2 >= 10)
                d2 = 0;
            retorno = "";
            retorno = "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + d1 + d2;
            return retorno;
        }
    }

    @Override
    public List<Entrega> save() {
        var pedidos = pedidoProducer.obterPedidosAguardandoEntrega();
        if (pedidos == null) {
            return Collections.emptyList();
        }
        pedidos.forEach(
                pedido -> {
                    Cliente cliente = clienteProducer.obterCliente(pedido.getIdCliente());
                    String cepRaiz = cliente.getEndereco().getCep().substring(0,5);
                    Optional<Entrega> optionalEntrega = entregaRepository.findEntregaByStatusAndCepRaiz(
                            Status.AGUARDANDO_ENTREGA.name(),
                            cepRaiz
                    );
                    optionalEntrega.ifPresentOrElse(
                            entrega -> {
                                entrega.getPedidos().add(pedido);
                                entregaRepository.save(entrega);
                            }, () -> {
                                Entrega entrega = new Entrega();
                                entrega.setId(UUID.randomUUID());
                                entrega.setCpfEntregador(GeraCpfCnpj.cpf());
                                entrega.setCepRaiz(cepRaiz);
                                entrega.setStatus(Status.AGUARDANDO_ENTREGA.name());
                                entrega.setPedidos(List.of(pedido));
                                entregaRepository.save(entrega);
                            }
                    );
                }
        );
        List<Entrega> entregas = entregaRepository.findEntregaByStatus(Status.AGUARDANDO_ENTREGA.name());
        entregas.forEach(entrega -> {
           entrega.setStatus(
                   Status.PREPARANDO_ENVIO.name()
           );
           entrega.getPedidos().forEach(
                   pedido -> pedidoProducer.atualizaStatus(pedido.getId(), new PedidoRequest(Status.PREPARANDO_ENVIO.name()))
           );
           entregaRepository.save(entrega);
        });
        return entregas;
    }

    @Override
    public Page<Entrega> findAll(Pageable pageable, Entrega entrega) {
        Example<Entrega> entregaExample = Example.of(entrega);
        return entregaRepository.findAll(entregaExample, pageable);
    }

    @Override
    public Entrega findById(UUID id) {
        return entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrado com o ID: " + id));
    }

    @Override
    public Entrega update(UUID id, Entrega entregaParam) {
        Entrega entrega = findById(id);
        if (entregaParam.getId() != null && !entrega.getId().equals(entregaParam.getId())) {
            throw new IllegalArgumentException("Não é possível alterar o id de um entrega.");
        }
        if (entregaParam.getCpfEntregador() != null && !entrega.getCpfEntregador().equals(entregaParam.getCpfEntregador())) {
            throw new IllegalArgumentException("Não é possível alterar o entregador de um entrega.");
        }
        if (entregaParam.getCepRaiz() != null && !entrega.getCepRaiz().equals(entregaParam.getCepRaiz())) {
            throw new IllegalArgumentException("Não é possível alterar o cep raiz de um entrega.");
        }
        if (entregaParam.getPedidos() != null && !new HashSet<>(entregaParam.getPedidos()).containsAll(entrega.getPedidos())) {
            throw new IllegalArgumentException("Não é possível alterar os pedidos um entrega.");
        }
        if (StringUtils.isNotEmpty(entregaParam.getStatus())) {
            if (!Status.contains(entregaParam.getStatus())) {
                throw new IllegalArgumentException("Status " + entregaParam.getStatus() + " não existe");
            }
            entrega.setStatus(entregaParam.getStatus());
        }
        if (Status.ENTREGUE.name().equals(entrega.getStatus())) {
            entrega.getPedidos().forEach(pedido -> {
                pedidoProducer.atualizaStatus(pedido.getId(), new PedidoRequest(Status.ENTREGUE.name()));
            });
        }
        return entregaRepository.save(entrega);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        entregaRepository.deleteById(id);
    }
}
