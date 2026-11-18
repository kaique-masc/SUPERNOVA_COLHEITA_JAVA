package br.com.fiap.supernova.repository;

import br.com.fiap.supernova.model.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findByUsuarioId(Long usuarioId, Pageable pageable);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);
}
