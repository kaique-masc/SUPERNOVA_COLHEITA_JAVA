package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.ProdutoRequest;
import br.com.fiap.supernova.dto.ProdutoResponse;
import br.com.fiap.supernova.exception.RecursoNaoEncontradoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.model.CategoriaProduto;
import br.com.fiap.supernova.model.Produto;
import br.com.fiap.supernova.repository.OfertaRepository;
import br.com.fiap.supernova.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final OfertaRepository ofertaRepository;

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(CategoriaProduto categoria, Pageable pageable) {
        Page<Produto> pagina = categoria == null
                ? produtoRepository.findAll(pageable)
                : produtoRepository.findByCategoria(categoria, pageable);
        return pagina.map(ProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (produtoRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new RegraNegocioException("Ja existe um produto com o nome " + request.nome() + ".");
        }
        Produto produto = new Produto(request.nome(), request.categoria(), request.unidade());
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        if (!produto.getNome().equalsIgnoreCase(request.nome())
                && produtoRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new RegraNegocioException("Ja existe um produto com o nome " + request.nome() + ".");
        }
        produto.setNome(request.nome());
        produto.setCategoria(request.categoria());
        produto.setUnidade(request.unidade());
        return ProdutoResponse.from(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        if (ofertaRepository.existsByProdutoId(id)) {
            throw new RegraNegocioException("Produto possui ofertas cadastradas e nao pode ser excluido.");
        }
        produtoRepository.delete(produto);
    }

    Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }
}
