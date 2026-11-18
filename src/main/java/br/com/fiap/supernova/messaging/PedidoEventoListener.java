package br.com.fiap.supernova.messaging;

import br.com.fiap.supernova.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEventoListener {

    private final NotificacaoService notificacaoService;

    @RabbitListener(queues = "${app.rabbitmq.fila-notificacoes}")
    public void receber(PedidoEvento evento) {
        log.info("Evento {} do pedido {} recebido", evento.tipo(), evento.pedidoId());
        notificacaoService.notificarEvento(evento);
    }
}
