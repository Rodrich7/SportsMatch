package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.ConfirmarEncuentroCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmarEncuentroHandler {

    private final EncuentroDeportivoRepository encuentros;

    public ConfirmarEncuentroHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(ConfirmarEncuentroCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.confirmar();
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
