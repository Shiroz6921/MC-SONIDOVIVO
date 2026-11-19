package com.sonidovivo.SonidoVivo.excepciones;

/**
 * Se lanza cuando un recurso solicitado no existe (responde HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
