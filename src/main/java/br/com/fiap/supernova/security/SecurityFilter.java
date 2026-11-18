package br.com.fiap.supernova.security;

import br.com.fiap.supernova.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            String email = tokenService.validarToken(token);
            if (email != null) {
                usuarioRepository.findByEmail(email)
                        .filter(u -> u.isEnabled())
                        .ifPresent(usuario -> {
                            var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            }
        }

        chain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
