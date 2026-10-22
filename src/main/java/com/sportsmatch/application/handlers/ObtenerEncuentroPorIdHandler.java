package com.sportsmatch.application.handlers;

import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.application.queries.ObtenerEncuentroPorIdQuery;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ObtenerEncuentroPorIdHandler {

    private final EncuentroDeportivoRepository encuentros;

    public ObtenerEncuentroPorIdHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(ObtenerEncuentroPorIdQuery consulta) {
        return encuentros.buscarPorId(consulta.encuentroId());
    }
}
