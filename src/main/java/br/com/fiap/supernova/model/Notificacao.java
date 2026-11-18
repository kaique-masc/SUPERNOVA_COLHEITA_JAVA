package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_sn_notificacao")
@Getter
@Setter
@NoArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(name = "id_pedido")
    private Long pedidoId;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Notificacao(Usuario usuario, String titulo, String mensagem, Long pedidoId) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.pedidoId = pedidoId;
    }
}
