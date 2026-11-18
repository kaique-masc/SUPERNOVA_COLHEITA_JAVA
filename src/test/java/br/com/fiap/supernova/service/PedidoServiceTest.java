package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.PedidoRequest;
import br.com.fiap.supernova.dto.PedidoResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.messaging.PedidoEvento;
import br.com.fiap.supernova.model.*;
import br.com.fiap.supernova.repository.CompradorRepository;
import br.com.fiap.supernova.repository.OfertaRepository;
import br.com.fiap.supernova.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private OfertaRepository ofertaRepository;
    @Mock
    private CompradorRepository compradorRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PedidoService pedidoService;

    private Comprador escola;
    private Usuario usuarioEscola;

    @BeforeEach
    void setUp() {
        escola = new Comprador();
        escola.setId(1L);
        escola.setNome("EMEF Teste");
        escola.setTipo(TipoComprador.ESCOLA);

        usuarioEscola = new Usuario("EMEF Teste", "escola@teste.com", "x", Role.COMPRADOR);
        usuarioEscola.setComprador(escola);

        lenient().when(compradorRepository.findById(1L)).thenReturn(Optional.of(escola));
    }

    @Test
    void criaPedidoBaixaEstoqueEPublicaEvento() {
        Oferta oferta = oferta(10L, produtor(LocalDate.now().plusYears(1)), "8.90", "100");
        when(ofertaRepository.findByIdParaAtualizar(10L)).thenReturn(Optional.of(oferta));

        PedidoResponse resposta = pedidoService.criar(usuarioEscola, pedido(10L, "30"));

        assertThat(resposta.valorTotal()).isEqualByComparingTo("267.00");
        assertThat(oferta.getQtdDisponivel()).isEqualByComparingTo("70");
        verify(pedidoRepository).save(any(Pedido.class));
        verify(eventPublisher).publishEvent(any(PedidoEvento.class));
    }

    @Test
    void marcaOfertaComoEsgotadaQuandoEstoqueZera() {
        Oferta oferta = oferta(10L, produtor(LocalDate.now().plusYears(1)), "2.00", "50");
        when(ofertaRepository.findByIdParaAtualizar(10L)).thenReturn(Optional.of(oferta));

        pedidoService.criar(usuarioEscola, pedido(10L, "50"));

        assertThat(oferta.getStatus()).isEqualTo(StatusOferta.ESGOTADA);
    }

    @Test
    void escolaNaoCompraDeProdutorComCafVencido() {
        Oferta oferta = oferta(10L, produtor(LocalDate.now().minusDays(1)), "2.00", "50");
        when(ofertaRepository.findByIdParaAtualizar(10L)).thenReturn(Optional.of(oferta));

        assertThatThrownBy(() -> pedidoService.criar(usuarioEscola, pedido(10L, "5")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("CAF");
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void restaurantePodeComprarDeProdutorSemCaf() {
        escola.setTipo(TipoComprador.RESTAURANTE);
        Oferta oferta = oferta(10L, produtor(null), "2.00", "50");
        when(ofertaRepository.findByIdParaAtualizar(10L)).thenReturn(Optional.of(oferta));

        PedidoResponse resposta = pedidoService.criar(usuarioEscola, pedido(10L, "5"));

        assertThat(resposta.valorTotal()).isEqualByComparingTo("10.00");
    }

    @Test
    void bloqueiaEstoqueInsuficiente() {
        Oferta oferta = oferta(10L, produtor(LocalDate.now().plusYears(1)), "2.00", "5");
        when(ofertaRepository.findByIdParaAtualizar(10L)).thenReturn(Optional.of(oferta));

        assertThatThrownBy(() -> pedidoService.criar(usuarioEscola, pedido(10L, "6")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    void bloqueiaOfertaVencida() {
        Oferta oferta = oferta(10L, produtor(LocalDate.now().plusYears(1)), "2.00", "50");
        oferta.setDataValidade(LocalDate.now().minusDays(1));
        when(ofertaRepository.findByIdParaAtualizar(10L)).thenReturn(Optional.of(oferta));

        assertThatThrownBy(() -> pedidoService.criar(usuarioEscola, pedido(10L, "1")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("venceu");
    }

    @Test
    void produtorNaoPodeFazerPedido() {
        Usuario produtor = new Usuario("Jose", "jose@teste.com", "x", Role.PRODUTOR);

        assertThatThrownBy(() -> pedidoService.criar(produtor, pedido(10L, "1")))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void cancelamentoDevolveEstoque() {
        Oferta oferta = oferta(10L, produtor(LocalDate.now().plusYears(1)), "2.00", "0");
        oferta.setStatus(StatusOferta.ESGOTADA);
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setComprador(escola);
        pedido.adicionarItem(new ItemPedido(oferta, new BigDecimal("20")));
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));

        pedidoService.atualizarStatus(usuarioEscola, 5L, StatusPedido.CANCELADO);

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        assertThat(oferta.getQtdDisponivel()).isEqualByComparingTo("20");
        assertThat(oferta.getStatus()).isEqualTo(StatusOferta.DISPONIVEL);
    }

    @Test
    void pedidoEntregueNaoVoltaParaPendente() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.ENTREGUE);

        assertThat(pedido.podeMudarPara(StatusPedido.PENDENTE)).isFalse();
        assertThat(pedido.podeMudarPara(StatusPedido.CANCELADO)).isFalse();
    }

    private Produtor produtor(LocalDate validadeCaf) {
        Produtor p = new Produtor();
        p.setId(3L);
        p.setNome("Produtor Teste");
        p.setNumeroCaf(validadeCaf == null ? null : "CAF-123");
        p.setValidadeCaf(validadeCaf);
        return p;
    }

    private Oferta oferta(Long id, Produtor produtor, String preco, String qtd) {
        Produto produto = new Produto("Feijao", CategoriaProduto.GRAO, UnidadeMedida.KG);
        Oferta o = new Oferta();
        o.setId(id);
        o.setProdutor(produtor);
        o.setProduto(produto);
        o.setPrecoUnitario(new BigDecimal(preco));
        o.setQtdDisponivel(new BigDecimal(qtd));
        o.setDataColheita(LocalDate.now().minusDays(1));
        o.setDataValidade(LocalDate.now().plusDays(10));
        return o;
    }

    private PedidoRequest pedido(Long ofertaId, String quantidade) {
        return new PedidoRequest(List.of(new PedidoRequest.Item(ofertaId, new BigDecimal(quantidade))));
    }
}
