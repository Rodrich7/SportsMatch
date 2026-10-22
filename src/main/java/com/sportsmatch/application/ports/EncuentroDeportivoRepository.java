package com.sportsmatch.application.ports;

import com.sportsmatch.domain.model.EncuentroDeportivo;

import java.util.Optional;

public interface EncuentroDeportivoRepository {

    Optional<EncuentroDeportivo> buscarPorId(Long id);

    boolean guardarSiNoExiste(EncuentroDeportivo encuentro);
}
