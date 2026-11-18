package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tb_sn_produtor")
@Getter
@Setter
@NoArgsConstructor
public class Produtor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "numero_caf", length = 30)
    private String numeroCaf;

    @Column(name = "validade_caf")
    private LocalDate validadeCaf;

    @Column(nullable = false, length = 80)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 15)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro = LocalDate.now();

    @Column(nullable = false)
    private boolean ativo = true;

    // regra do PNAE: so vende para escola quem tem CAF valido
    public boolean isAptoPnae(LocalDate data) {
        return ativo && numeroCaf != null && validadeCaf != null && !validadeCaf.isBefore(data);
    }
}
