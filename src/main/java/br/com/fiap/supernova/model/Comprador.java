package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_sn_comprador")
@Getter
@Setter
@NoArgsConstructor
public class Comprador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComprador tipo;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 80)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;

    // so para escolas: valor anual repassado pelo FNDE
    @Column(name = "verba_pnae_anual", precision = 12, scale = 2)
    private BigDecimal verbaPnaeAnual;

    @Column(length = 120)
    private String email;

    public boolean isEscola() {
        return tipo == TipoComprador.ESCOLA;
    }
}
