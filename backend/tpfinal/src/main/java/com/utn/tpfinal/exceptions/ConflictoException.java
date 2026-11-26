package com.utn.tpfinal.exceptions;

// Se lanza cuando el pedido es válido pero choca con el estado actual de los datos
// (ej: asignar un paciente a una cama que ya está ocupada). Se responde con HTTP 409.
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
