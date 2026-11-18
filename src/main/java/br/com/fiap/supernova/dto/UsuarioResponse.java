package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.Role;
import br.com.fiap.supernova.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, Role role, Long produtorId, Long compradorId) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getRole(),
                u.getProdutor() != null ? u.getProdutor().getId() : null,
                u.getComprador() != null ? u.getComprador().getId() : null);
    }
}
