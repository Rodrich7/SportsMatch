package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.CerrarInscripcionesCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CerrarInscripcionesHandler {

    private final EncuentroDeportivoRepository encuentros;

    public CerrarInscripcionesHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(CerrarInscripcionesCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.cerrarInscripciones();
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
