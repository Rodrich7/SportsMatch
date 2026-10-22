package com.sportsmatch.application.handlers;

import com.sportsmatch.application.queries.ObtenerEncuentroPorIdQuery;
import com.sportsmatch.domain.model.EncuentroDeportivo;

import java.util.Map;
import java.util.Optional;

public class ObtenerEncuentroPorIdHandler {

    private final Map<Long, EncuentroDeportivo> encuentros;

    public ObtenerEncuentroPorIdHandler(Map<Long, EncuentroDeportivo> encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(ObtenerEncuentroPorIdQuery consulta) {
        return Optional.ofNullable(encuentros.get(consulta.encuentroId()));
    }
}
