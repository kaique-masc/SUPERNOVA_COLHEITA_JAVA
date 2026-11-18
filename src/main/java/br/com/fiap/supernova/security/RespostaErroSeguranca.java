package br.com.fiap.supernova.security;

import br.com.fiap.supernova.exception.ErroResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 401 e 403 no mesmo formato de erro do resto da API
@Component
@RequiredArgsConstructor
public class RespostaErroSeguranca implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        escrever(response, request, HttpStatus.UNAUTHORIZED, "Token ausente, invalido ou expirado.");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escrever(response, request, HttpStatus.FORBIDDEN, "Seu perfil nao tem permissao para este recurso.");
    }

    private void escrever(HttpServletResponse response, HttpServletRequest request,
                          HttpStatus status, String mensagem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(),
                ErroResponse.of(status.value(), status.getReasonPhrase(), mensagem, request.getRequestURI()));
    }
}
