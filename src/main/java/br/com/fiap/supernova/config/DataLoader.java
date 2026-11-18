package br.com.fiap.supernova.config;

import br.com.fiap.supernova.model.*;
import br.com.fiap.supernova.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final String SENHA_PADRAO = "123456";

    private final UsuarioRepository usuarioRepository;
    private final ProdutorRepository produtorRepository;
    private final CompradorRepository compradorRepository;
    private final ProdutoRepository produtoRepository;
    private final OfertaRepository ofertaRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        LocalDate hoje = LocalDate.now();

        usuario("Gestor PNAE Mogi das Cruzes", "gestor@supernova.com", Role.GESTOR, null, null);

        Produtor jose = produtor("Jose Aparecido da Silva", "11122233344", "CAF-SP-2025-000123",
                hoje.plusYears(1), "Mogi das Cruzes", "11987650001");
        Produtor maria = produtor("Maria das Gracas Oliveira", "22233344455", "CAF-SP-2025-000456",
                hoje.plusMonths(8), "Registro", "13987650002");
        Produtor terraViva = produtor("Cooperativa Familiar Terra Viva", "33344455566", "CAF-SP-2024-000789",
                hoje.plusMonths(3), "Ibiuna", "15987650003");
        Produtor benedita = produtor("Benedita Souza Lima", "55566677788", "CAF-SP-2023-001213",
                hoje.minusMonths(2), "Registro", "13987650005");

        usuario(jose.getNome(), "jose@supernova.com", Role.PRODUTOR, jose, null);
        usuario(maria.getNome(), "maria@supernova.com", Role.PRODUTOR, maria, null);
        usuario(terraViva.getNome(), "terraviva@supernova.com", Role.PRODUTOR, terraViva, null);
        usuario(benedita.getNome(), "benedita@supernova.com", Role.PRODUTOR, benedita, null);

        Comprador escola = comprador("EMEF Professora Maria Aparecida", TipoComprador.ESCOLA, "10111222000101",
                "Mogi das Cruzes", new BigDecimal("120000.00"));
        Comprador restaurante = comprador("Restaurante Sabor da Terra", TipoComprador.RESTAURANTE,
                "40444555000104", "Mogi das Cruzes", null);

        usuario(escola.getNome(), "escola@supernova.com", Role.COMPRADOR, null, escola);
        usuario(restaurante.getNome(), "restaurante@supernova.com", Role.COMPRADOR, null, restaurante);

        Map<String, Produto> produtos = new HashMap<>();
        produto(produtos, "Alface Crespa", CategoriaProduto.HORTALICA, UnidadeMedida.MACO);
        produto(produtos, "Tomate", CategoriaProduto.LEGUME, UnidadeMedida.KG);
        produto(produtos, "Banana Prata", CategoriaProduto.FRUTA, UnidadeMedida.KG);
        produto(produtos, "Cenoura", CategoriaProduto.LEGUME, UnidadeMedida.KG);
        produto(produtos, "Feijao Carioca", CategoriaProduto.GRAO, UnidadeMedida.KG);
        produto(produtos, "Ovos Caipira", CategoriaProduto.OUTRO, UnidadeMedida.DUZIA);
        produto(produtos, "Couve Manteiga", CategoriaProduto.HORTALICA, UnidadeMedida.MACO);
        produto(produtos, "Mandioca", CategoriaProduto.LEGUME, UnidadeMedida.KG);
        produto(produtos, "Laranja Pera", CategoriaProduto.FRUTA, UnidadeMedida.KG);
        produto(produtos, "Abobora Cabotia", CategoriaProduto.LEGUME, UnidadeMedida.KG);

        Oferta alface = oferta(jose, produtos.get("Alface Crespa"), "2.70", "300", hoje.minusDays(1), hoje.plusDays(6));
        oferta(jose, produtos.get("Tomate"), "6.50", "200", hoje, hoje.plusDays(7));
        Oferta banana = oferta(maria, produtos.get("Banana Prata"), "4.80", "400", hoje.minusDays(2), hoje.plusDays(10));
        oferta(maria, produtos.get("Laranja Pera"), "3.20", "700", hoje.minusDays(1), hoje.plusDays(15));
        oferta(maria, produtos.get("Mandioca"), "3.40", "500", hoje.minusDays(3), hoje.plusDays(20));
        Oferta feijao = oferta(terraViva, produtos.get("Feijao Carioca"), "8.90", "5000", hoje.minusDays(30), hoje.plusDays(150));
        Oferta cenoura = oferta(terraViva, produtos.get("Cenoura"), "4.10", "450", hoje.minusDays(2), hoje.plusDays(12));
        oferta(terraViva, produtos.get("Ovos Caipira"), "11.50", "200", hoje.minusDays(1), hoje.plusDays(20));
        oferta(terraViva, produtos.get("Abobora Cabotia"), "3.90", "350", hoje.minusDays(4), hoje.plusDays(25));
        oferta(benedita, produtos.get("Couve Manteiga"), "2.20", "150", hoje, hoje.plusDays(5));

        // historico: compras ja entregues para a escola neste ano
        pedido(escola, StatusPedido.ENTREGUE, hoje.minusDays(20), Map.of(feijao, "2000"));
        pedido(escola, StatusPedido.ENTREGUE, hoje.minusDays(10), Map.of(banana, "200", alface, "100"));
        pedido(restaurante, StatusPedido.CONFIRMADO, hoje.minusDays(1), Map.of(cenoura, "50"));

        log.info("Dados de exemplo carregados. Senha de todos os usuarios: {}", SENHA_PADRAO);
    }

    private void usuario(String nome, String email, Role role, Produtor produtor, Comprador comprador) {
        Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(SENHA_PADRAO), role);
        usuario.setProdutor(produtor);
        usuario.setComprador(comprador);
        usuarioRepository.save(usuario);
    }

    private Produtor produtor(String nome, String cpf, String caf, LocalDate validadeCaf, String municipio,
                              String telefone) {
        Produtor p = new Produtor();
        p.setNome(nome);
        p.setCpf(cpf);
        p.setNumeroCaf(caf);
        p.setValidadeCaf(validadeCaf);
        p.setMunicipio(municipio);
        p.setUf("SP");
        p.setTelefone(telefone);
        return produtorRepository.save(p);
    }

    private Comprador comprador(String nome, TipoComprador tipo, String cnpj, String municipio, BigDecimal verba) {
        Comprador c = new Comprador();
        c.setNome(nome);
        c.setTipo(tipo);
        c.setCnpj(cnpj);
        c.setMunicipio(municipio);
        c.setUf("SP");
        c.setVerbaPnaeAnual(verba);
        return compradorRepository.save(c);
    }

    private void produto(Map<String, Produto> produtos, String nome, CategoriaProduto categoria, UnidadeMedida unidade) {
        produtos.put(nome, produtoRepository.save(new Produto(nome, categoria, unidade)));
    }

    private Oferta oferta(Produtor produtor, Produto produto, String preco, String qtd, LocalDate colheita,
                          LocalDate validade) {
        Oferta o = new Oferta();
        o.setProdutor(produtor);
        o.setProduto(produto);
        o.setPrecoUnitario(new BigDecimal(preco));
        o.setQtdDisponivel(new BigDecimal(qtd));
        o.setDataColheita(colheita);
        o.setDataValidade(validade);
        return ofertaRepository.save(o);
    }

    private void pedido(Comprador comprador, StatusPedido status, LocalDate data, Map<Oferta, String> itens) {
        Pedido pedido = new Pedido();
        pedido.setComprador(comprador);
        pedido.setStatus(status);
        pedido.setDataPedido(data.atTime(10, 0));
        itens.forEach((oferta, qtd) -> {
            BigDecimal quantidade = new BigDecimal(qtd);
            pedido.adicionarItem(new ItemPedido(oferta, quantidade));
            oferta.baixarEstoque(quantidade);
        });
        pedidoRepository.save(pedido);
    }
}
