package br.com.fiap.supernova.repository;

import br.com.fiap.supernova.model.CategoriaProduto;
import br.com.fiap.supernova.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Page<Produto> findByCategoria(CategoriaProduto categoria, Pageable pageable);
}
