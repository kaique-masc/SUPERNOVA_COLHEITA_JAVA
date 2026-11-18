package br.com.fiap.supernova.security;

import br.com.fiap.supernova.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private static final String EMISSOR = "supernova-colheita-api";

    private final Algorithm algoritmo;
    private final long expiracaoHoras;

    public TokenService(@Value("${app.jwt.secret}") String secret,
                        @Value("${app.jwt.expiracao-horas}") long expiracaoHoras) {
        this.algoritmo = Algorithm.HMAC256(secret);
        this.expiracaoHoras = expiracaoHoras;
    }

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getEmail())
                .withClaim("id", usuario.getId())
                .withClaim("role", usuario.getRole().name())
                .withIssuedAt(Instant.now())
                .withExpiresAt(dataExpiracao())
                .sign(algoritmo);
    }

    // retorna o e-mail do token, ou null se o token for invalido/expirado
    public String validarToken(String token) {
        try {
            return JWT.require(algoritmo)
                    .withIssuer(EMISSOR)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public Instant dataExpiracao() {
        return Instant.now().plus(expiracaoHoras, ChronoUnit.HOURS);
    }
}
