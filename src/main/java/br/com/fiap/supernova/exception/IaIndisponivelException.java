package br.com.fiap.supernova.exception;

public class IaIndisponivelException extends RuntimeException {

    public IaIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
