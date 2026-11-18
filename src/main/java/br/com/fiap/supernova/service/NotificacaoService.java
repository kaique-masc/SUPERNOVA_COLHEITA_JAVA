package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.NotificacaoResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RecursoNaoEncontradoException;
import br.com.fiap.supernova.messaging.PedidoEvento;
import br.com.fiap.supernova.model.Notificacao;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.repository.NotificacaoRepository;
import br.com.fiap.supernova.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void notificarEvento(PedidoEvento evento) {
        switch (evento.tipo()) {
            case PEDIDO_CRIADO -> evento.produtorIds().forEach(produtorId ->
                    usuarioRepository.findByProdutorId(produtorId).ifPresent(u -> salvar(u,
                            "Novo pedido recebido",
                            evento.compradorNome() + " fez o pedido #" + evento.pedidoId()
                                    + " com produtos seus. Confirme para seguir com a entrega.",
                            evento.pedidoId())));
            case STATUS_ALTERADO -> {
                String mensagem = "O pedido #" + evento.pedidoId() + " mudou para " + evento.status() + ".";
                usuarioRepository.findByCompradorId(evento.compradorId())
                        .ifPresent(u -> salvar(u, "Pedido atualizado", mensagem, evento.pedidoId()));
                evento.produtorIds().forEach(produtorId -> usuarioRepository.findByProdutorId(produtorId)
                        .ifPresent(u -> salvar(u, "Pedido atualizado", mensagem, evento.pedidoId())));
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoResponse> listar(Usuario usuario, Pageable pageable) {
        return notificacaoRepository.findByUsuarioId(usuario.getId(), pageable).map(NotificacaoResponse::from);
    }

    @Transactional
    public NotificacaoResponse marcarComoLida(Usuario usuario, Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificacao", id));
        if (!notificacao.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Esta notificacao nao e sua.");
        }
        notificacao.setLida(true);
        return NotificacaoResponse.from(notificacao);
    }

    private void salvar(Usuario usuario, String titulo, String mensagem, Long pedidoId) {
        notificacaoRepository.save(new Notificacao(usuario, titulo, mensagem, pedidoId));
    }
}
