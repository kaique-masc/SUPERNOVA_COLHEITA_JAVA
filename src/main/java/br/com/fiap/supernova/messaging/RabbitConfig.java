package br.com.fiap.supernova.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange pedidosExchange(@Value("${app.rabbitmq.exchange}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue filaNotificacoes(@Value("${app.rabbitmq.fila-notificacoes}") String fila) {
        return QueueBuilder.durable(fila).build();
    }

    // a fila recebe todos os eventos de pedido (pedido.pedido_criado, pedido.status_alterado)
    @Bean
    public Binding bindingNotificacoes(Queue filaNotificacoes, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(filaNotificacoes).to(pedidosExchange).with("pedido.#");
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
