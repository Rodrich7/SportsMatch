package com.sportsmatch.application.usecase;

import com.sportsmatch.domain.model.EncuentroDeportivo;

import java.time.LocalDateTime;

public class CrearEncuentroDeportivo {

    public EncuentroDeportivo ejecutar(String deporte, LocalDateTime fechaHora, String cancha,
                                       int cupoMinimo, int cupoMaximo, String organizador) {
        return new EncuentroDeportivo(null, deporte, fechaHora, cancha, cupoMinimo, cupoMaximo, organizador);
    }
}
