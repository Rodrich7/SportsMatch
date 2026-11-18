package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.CancelarEncuentroCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CancelarEncuentroHandler {

    private final EncuentroDeportivoRepository encuentros;

    public CancelarEncuentroHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(CancelarEncuentroCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.cancelar();
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
