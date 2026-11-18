package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.Produtor;

import java.time.LocalDate;

public record ProdutorResponse(Long id, String nome, String cpf, String numeroCaf, LocalDate validadeCaf,
                               String municipio, String uf, String telefone, String email,
                               boolean ativo, boolean aptoPnae) {

    public static ProdutorResponse from(Produtor p) {
        return new ProdutorResponse(p.getId(), p.getNome(), p.getCpf(), p.getNumeroCaf(), p.getValidadeCaf(),
                p.getMunicipio(), p.getUf(), p.getTelefone(), p.getEmail(), p.isAtivo(),
                p.isAptoPnae(LocalDate.now()));
    }
}
