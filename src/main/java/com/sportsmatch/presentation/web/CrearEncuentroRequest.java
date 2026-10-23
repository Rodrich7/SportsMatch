package com.sportsmatch.presentation.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CrearEncuentroRequest(
        @NotBlank(message = "El deporte es obligatorio") String deporte,
        @NotNull(message = "La fecha y hora son obligatorias") LocalDateTime fechaHora,
        @NotBlank(message = "La cancha es obligatoria") String cancha,
        @NotNull(message = "El cupo mínimo es obligatorio")
        @Positive(message = "El cupo mínimo debe ser mayor que cero") Integer cupoMinimo,
        @NotNull(message = "El cupo máximo es obligatorio")
        @Positive(message = "El cupo máximo debe ser mayor que cero") Integer cupoMaximo,
        @NotBlank(message = "El organizador es obligatorio") String organizador) {
}
