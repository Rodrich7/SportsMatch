package com.sportsmatch.presentation.web;

import java.time.LocalDateTime;

public record CrearEncuentroRequest(String deporte, LocalDateTime fechaHora, String cancha,
                                    int cupoMinimo, int cupoMaximo, String organizador) {
}
