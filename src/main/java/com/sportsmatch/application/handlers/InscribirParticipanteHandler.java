package com.sportsmatch.application.handlers;

import com.sportsmatch.application.commands.InscribirParticipanteCommand;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InscribirParticipanteHandler {

    private final EncuentroDeportivoRepository encuentros;

    public InscribirParticipanteHandler(EncuentroDeportivoRepository encuentros) {
        this.encuentros = encuentros;
    }

    public Optional<EncuentroDeportivo> manejar(InscribirParticipanteCommand comando) {
        Optional<EncuentroDeportivo> encontrado = encuentros.buscarPorId(comando.encuentroId());
        encontrado.ifPresent(encuentro -> {
            encuentro.inscribirParticipante(comando.participante());
            encuentros.guardar(encuentro);
        });
        return encontrado;
    }
}
