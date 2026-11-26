package com.sportsmatch.presentation.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FinalizarEncuentroRequest(
        @NotBlank(message = "El resultado es obligatorio")
        @Size(max = 200, message = "El resultado no puede superar los 200 caracteres")
        String resultado) {
}
