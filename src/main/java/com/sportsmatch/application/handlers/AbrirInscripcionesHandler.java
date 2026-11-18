package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.AbrirInscripcionesCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AbrirInscripcionesHandler {

    private final EncuentroDeportivoRepository encuentros;

    public AbrirInscripcionesHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(AbrirInscripcionesCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.abrirInscripciones();
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
