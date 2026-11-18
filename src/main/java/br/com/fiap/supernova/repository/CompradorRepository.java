package br.com.fiap.supernova.repository;

import br.com.fiap.supernova.model.Comprador;
import br.com.fiap.supernova.model.TipoComprador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    boolean existsByCnpj(String cnpj);

    Page<Comprador> findByTipo(TipoComprador tipo, Pageable pageable);

    List<Comprador> findByTipoOrderByNome(TipoComprador tipo);
}
