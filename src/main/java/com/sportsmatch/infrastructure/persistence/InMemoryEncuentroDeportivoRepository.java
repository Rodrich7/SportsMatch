package com.sportsmatch.infrastructure.persistence;

import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryEncuentroDeportivoRepository implements EncuentroDeportivoRepository {

    private final ConcurrentHashMap<Long, EncuentroDeportivo> encuentros = new ConcurrentHashMap<>();
    private final AtomicLong siguienteId = new AtomicLong(1);

    @Override
    public Optional<EncuentroDeportivo> buscarPorId(Long id) {
        return Optional.ofNullable(encuentros.get(id));
    }

    @Override
    public EncuentroDeportivo guardarNuevo(EncuentroDeportivo encuentro) {
        EncuentroDeportivo conId = EncuentroDeportivo.restaurar(siguienteId.getAndIncrement(),
                encuentro.getDeporte(), encuentro.getFechaHora(), encuentro.getCancha(),
                encuentro.getCupoMinimo(), encuentro.getCupoMaximo(), encuentro.getOrganizador(),
                encuentro.getEstado(), encuentro.getParticipantes());
        encuentros.put(conId.getId(), conId);
        return conId;
    }

    @Override
    public void guardar(EncuentroDeportivo encuentro) {
        encuentros.put(encuentro.getId(), encuentro);
    }
}
