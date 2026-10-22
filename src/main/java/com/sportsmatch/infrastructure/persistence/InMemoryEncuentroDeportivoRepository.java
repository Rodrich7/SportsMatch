package com.sportsmatch.infrastructure.persistence;

import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryEncuentroDeportivoRepository implements EncuentroDeportivoRepository {

    private final ConcurrentHashMap<Long, EncuentroDeportivo> encuentros = new ConcurrentHashMap<>();

    @Override
    public Optional<EncuentroDeportivo> buscarPorId(Long id) {
        return Optional.ofNullable(encuentros.get(id));
    }

    @Override
    public boolean guardarSiNoExiste(EncuentroDeportivo encuentro) {
        return encuentros.putIfAbsent(encuentro.getId(), encuentro) == null;
    }
}
