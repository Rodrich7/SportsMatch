package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.CrearEncuentroCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

@Service
public class CrearEncuentroHandler {

    private final EncuentroDeportivoRepository encuentros;

    public CrearEncuentroHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public EncuentroDeportivo manejar(CrearEncuentroCommand comando) {
        EncuentroDeportivo encuentro = new EncuentroDeportivo(null, comando.deporte(), comando.fechaHora(),
                comando.cancha(), comando.cupoMinimo(), comando.cupoMaximo(), comando.organizador());
        return encuentros.guardarNuevo(encuentro);
    }
}
