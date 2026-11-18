package br.com.fiap.supernova.repository;

import br.com.fiap.supernova.model.Oferta;
import br.com.fiap.supernova.model.StatusOferta;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    @Query("""
            select o from Oferta o
             where (:status is null or o.status = :status)
               and (:produtoId is null or o.produto.id = :produtoId)
               and (:produtorId is null or o.produtor.id = :produtorId)
               and (:uf is null or o.produtor.uf = :uf)
            """)
    Page<Oferta> buscar(@Param("status") StatusOferta status,
                        @Param("produtoId") Long produtoId,
                        @Param("produtorId") Long produtorId,
                        @Param("uf") String uf,
                        Pageable pageable);

    // trava a linha para dois pedidos nao comprarem o mesmo estoque ao mesmo tempo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Oferta o where o.id = :id")
    Optional<Oferta> findByIdParaAtualizar(@Param("id") Long id);

    @Query("""
            select o from Oferta o
             where o.status = br.com.fiap.supernova.model.StatusOferta.DISPONIVEL
               and o.dataValidade >= :hoje
               and o.qtdDisponivel > 0
               and o.produtor.uf = :uf
             order by o.dataValidade
            """)
    List<Oferta> findDisponiveisNaUf(@Param("uf") String uf, @Param("hoje") LocalDate hoje);

    boolean existsByProdutoId(Long produtoId);
}
