package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.FinalizarEncuentroCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FinalizarEncuentroHandler {

    private final EncuentroDeportivoRepository encuentros;

    public FinalizarEncuentroHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(FinalizarEncuentroCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.finalizar(comando.resultado());
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
