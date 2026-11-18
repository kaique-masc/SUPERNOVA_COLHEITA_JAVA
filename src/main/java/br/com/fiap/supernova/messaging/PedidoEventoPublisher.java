package br.com.fiap.supernova.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEventoPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(PedidoEvento evento) {
        try {
            rabbitTemplate.convertAndSend(exchange, evento.routingKey(), evento);
            log.info("Evento {} do pedido {} publicado", evento.tipo(), evento.pedidoId());
        } catch (AmqpException e) {
            // o pedido ja esta salvo; so a notificacao fica sem ser enviada
            log.warn("Nao foi possivel publicar o evento do pedido {}: {}", evento.pedidoId(), e.getMessage());
        }
    }
}
