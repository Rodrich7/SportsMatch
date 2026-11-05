package com.sportsmatch.application.commands;

import java.time.LocalDateTime;

public record CrearEncuentroCommand(String deporte, LocalDateTime fechaHora, String cancha,
                                    int cupoMinimo, int cupoMaximo, String organizador) {
}
