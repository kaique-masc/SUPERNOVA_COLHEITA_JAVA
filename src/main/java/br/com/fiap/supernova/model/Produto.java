package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_sn_produto")
@Getter
@Setter
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaProduto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeMedida unidade;

    public Produto(String nome, CategoriaProduto categoria, UnidadeMedida unidade) {
        this.nome = nome;
        this.categoria = categoria;
        this.unidade = unidade;
    }
}
