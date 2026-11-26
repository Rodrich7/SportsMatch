package com.sportsmatch.presentation.web.dto;

import com.sportsmatch.domain.model.EstadoEncuentro;

import java.time.LocalDateTime;
import java.util.List;

public record EncuentroDeportivoResponse(
        Long id,
        String deporte,
        LocalDateTime fechaHora,
        String cancha,
        int cupoMinimo,
        int cupoMaximo,
        String organizador,
        EstadoEncuentro estado,
        List<String> participantes,
        String resultado
) {
    public EncuentroDeportivoResponse {
        participantes = List.copyOf(participantes);
    }
}
