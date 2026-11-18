package br.com.fiap.supernova.repository;

import br.com.fiap.supernova.model.Pedido;
import br.com.fiap.supernova.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByCompradorId(Long compradorId, Pageable pageable);

    @Query(value = """
            select p from Pedido p
             where exists (select 1 from ItemPedido i
                            where i.pedido = p and i.oferta.produtor.id = :produtorId)
            """)
    Page<Pedido> findByProdutorId(@Param("produtorId") Long produtorId, Pageable pageable);

    @Query("""
            select coalesce(sum(p.valorTotal), 0) from Pedido p
             where p.comprador.id = :compradorId
               and p.status = :status
               and p.dataPedido >= :inicio and p.dataPedido < :fim
            """)
    BigDecimal somarPorCompradorEStatus(@Param("compradorId") Long compradorId,
                                        @Param("status") StatusPedido status,
                                        @Param("inicio") LocalDateTime inicio,
                                        @Param("fim") LocalDateTime fim);

    boolean existsByCompradorId(Long compradorId);
}
