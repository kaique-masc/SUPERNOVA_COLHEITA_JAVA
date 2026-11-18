package br.com.fiap.supernova.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " " + id + " nao encontrado(a).");
    }
}
