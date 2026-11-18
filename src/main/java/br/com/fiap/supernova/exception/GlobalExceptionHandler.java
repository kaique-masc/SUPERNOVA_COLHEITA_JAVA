package br.com.fiap.supernova.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErroResponse.CampoErro> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErroResponse.CampoErro(f.getField(), f.getDefaultMessage()))
                .toList();
        ErroResponse erro = new ErroResponse(LocalDateTime.now(), 400, "Dados invalidos",
                "Um ou mais campos estao invalidos.", req.getRequestURI(), campos);
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    @ExceptionHandler({AcessoNegadoException.class, AccessDeniedException.class})
    public ResponseEntity<ErroResponse> acessoNegado(RuntimeException ex, HttpServletRequest req) {
        String mensagem = ex instanceof AcessoNegadoException
                ? ex.getMessage()
                : "Seu perfil nao tem permissao para este recurso.";
        return resposta(HttpStatus.FORBIDDEN, mensagem, req);
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<ErroResponse> credenciais(RuntimeException ex, HttpServletRequest req) {
        return resposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha invalidos.", req);
    }

    @ExceptionHandler(IaIndisponivelException.class)
    public ResponseEntity<ErroResponse> ia(IaIndisponivelException ex, HttpServletRequest req) {
        log.warn("Falha na IA: {}", ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
        return resposta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        return resposta(HttpStatus.CONFLICT,
                "Operacao viola uma restricao do banco (registro duplicado ou em uso).", req);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResponse> requisicaoInvalida(Exception ex, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisicao invalida: verifique o JSON e os parametros.", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> rotaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, "Endpoint nao encontrado.", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> erroInterno(Exception ex, HttpServletRequest req) {
        log.error("Erro nao tratado em {}", req.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.", req);
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErroResponse.of(status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI()));
    }
}
