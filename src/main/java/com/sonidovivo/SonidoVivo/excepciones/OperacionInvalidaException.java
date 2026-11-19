package com.sonidovivo.SonidoVivo.excepciones;

/**
 * Se lanza cuando una operacion de negocio no se puede cumplir,
 * por ejemplo vender mas unidades que el stock disponible (HTTP 400).
 */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
