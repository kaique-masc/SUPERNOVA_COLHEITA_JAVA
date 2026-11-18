package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.PedidoRequest;
import br.com.fiap.supernova.dto.PedidoResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RecursoNaoEncontradoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.messaging.PedidoEvento;
import br.com.fiap.supernova.model.*;
import br.com.fiap.supernova.repository.CompradorRepository;
import br.com.fiap.supernova.repository.OfertaRepository;
import br.com.fiap.supernova.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final OfertaRepository ofertaRepository;
    private final CompradorRepository compradorRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PedidoResponse criar(Usuario usuario, PedidoRequest request) {
        if (usuario.getComprador() == null) {
            throw new AcessoNegadoException("Apenas compradores podem fazer pedidos.");
        }
        Comprador comprador = compradorRepository.findById(usuario.getComprador().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comprador", usuario.getComprador().getId()));

        return PedidoResponse.from(registrar(comprador, request.itens()));
    }

    Pedido registrar(Comprador comprador, List<PedidoRequest.Item> itens) {
        Pedido pedido = montarPedido(comprador, itens);
        pedidoRepository.save(pedido);
        eventPublisher.publishEvent(PedidoEvento.de(PedidoEvento.TipoEvento.PEDIDO_CRIADO, pedido));
        return pedido;
    }

    private Pedido montarPedido(Comprador comprador, List<PedidoRequest.Item> itens) {
        LocalDate hoje = LocalDate.now();
        Set<Long> ofertasNoPedido = new HashSet<>();
        Pedido pedido = new Pedido();
        pedido.setComprador(comprador);

        for (PedidoRequest.Item item : itens) {
            if (!ofertasNoPedido.add(item.ofertaId())) {
                throw new RegraNegocioException("A oferta " + item.ofertaId() + " aparece mais de uma vez no pedido.");
            }

            Oferta oferta = ofertaRepository.findByIdParaAtualizar(item.ofertaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Oferta", item.ofertaId()));

            validarItem(comprador, oferta, item, hoje);

            pedido.adicionarItem(new ItemPedido(oferta, item.quantidade()));
            oferta.baixarEstoque(item.quantidade());
        }
        return pedido;
    }

    private void validarItem(Comprador comprador, Oferta oferta, PedidoRequest.Item item, LocalDate hoje) {
        if (oferta.getStatus() != StatusOferta.DISPONIVEL) {
            throw new RegraNegocioException("A oferta " + oferta.getId() + " nao esta disponivel (status: "
                    + oferta.getStatus() + ").");
        }
        if (oferta.isVencida(hoje)) {
            throw new RegraNegocioException("A oferta " + oferta.getId() + " venceu em " + oferta.getDataValidade() + ".");
        }

        if (comprador.isEscola() && !oferta.getProdutor().isAptoPnae(hoje)) {
            throw new RegraNegocioException("O produtor " + oferta.getProdutor().getNome()
                    + " nao possui CAF valido e nao pode vender para escolas pelo PNAE.");
        }
        if (item.quantidade().compareTo(oferta.getQtdDisponivel()) > 0) {
            throw new RegraNegocioException("Estoque insuficiente na oferta " + oferta.getId() + ". Disponivel: "
                    + oferta.getQtdDisponivel() + ", solicitado: " + item.quantidade() + ".");
        }
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(Usuario usuario, Pageable pageable) {
        Page<Pedido> pagina = switch (usuario.getRole()) {
            case GESTOR -> pedidoRepository.findAll(pageable);
            case COMPRADOR -> pedidoRepository.findByCompradorId(usuario.getComprador().getId(), pageable);
            case PRODUTOR -> pedidoRepository.findByProdutorId(usuario.getProdutor().getId(), pageable);
        };
        return pagina.map(PedidoResponse::from);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Usuario usuario, Long id) {
        Pedido pedido = buscarEntidade(id);
        validarAcesso(usuario, pedido);
        return PedidoResponse.from(pedido);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Usuario usuario, Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarEntidade(id);
        validarAcesso(usuario, pedido);
        validarQuemPodeMudar(usuario, novoStatus);

        if (!pedido.podeMudarPara(novoStatus)) {
            throw new RegraNegocioException("Nao e permitido mudar o pedido de " + pedido.getStatus()
                    + " para " + novoStatus + ".");
        }

        if (novoStatus == StatusPedido.CANCELADO) {
            pedido.getItens().forEach(i -> i.getOferta().devolverEstoque(i.getQuantidade()));
        }
        pedido.setStatus(novoStatus);

        eventPublisher.publishEvent(PedidoEvento.de(PedidoEvento.TipoEvento.STATUS_ALTERADO, pedido));
        return PedidoResponse.from(pedido);
    }

    private void validarQuemPodeMudar(Usuario usuario, StatusPedido novoStatus) {
        boolean permitido = switch (usuario.getRole()) {
            case GESTOR -> true;
            case PRODUTOR -> novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.ENTREGUE;
            case COMPRADOR -> novoStatus == StatusPedido.CANCELADO;
        };
        if (!permitido) {
            throw new AcessoNegadoException("Seu perfil nao pode mudar o pedido para " + novoStatus + ".");
        }
    }

    private void validarAcesso(Usuario usuario, Pedido pedido) {
        boolean permitido = switch (usuario.getRole()) {
            case GESTOR -> true;
            case COMPRADOR -> pedido.getComprador().getId().equals(usuario.getComprador().getId());
            case PRODUTOR -> pedido.getItens().stream()
                    .anyMatch(i -> i.getOferta().getProdutor().getId().equals(usuario.getProdutor().getId()));
        };
        if (!permitido) {
            throw new AcessoNegadoException("Voce nao tem acesso a este pedido.");
        }
    }

    private Pedido buscarEntidade(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
    }
}
