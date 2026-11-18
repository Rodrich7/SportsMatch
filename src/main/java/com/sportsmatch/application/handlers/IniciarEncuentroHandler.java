package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.IniciarEncuentroCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IniciarEncuentroHandler {

    private final EncuentroDeportivoRepository encuentros;

    public IniciarEncuentroHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(IniciarEncuentroCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.iniciar();
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
