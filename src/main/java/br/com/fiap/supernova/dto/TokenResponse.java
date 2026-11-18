package br.com.fiap.supernova.dto;

import java.time.Instant;

public record TokenResponse(String token, String tipo, Instant expiraEm, UsuarioResponse usuario) {
}
