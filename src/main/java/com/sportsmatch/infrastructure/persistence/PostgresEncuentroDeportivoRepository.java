package com.sportsmatch.infrastructure.persistence;

import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class PostgresEncuentroDeportivoRepository implements EncuentroDeportivoRepository {

    private final SpringDataEncuentroDeportivoRepository repository;

    public PostgresEncuentroDeportivoRepository(SpringDataEncuentroDeportivoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<EncuentroDeportivo> buscarPorId(Long id) {
        return repository.findById(id).map(this::aDominio);
    }

    @Override
    public EncuentroDeportivo guardarNuevo(EncuentroDeportivo encuentro) {
        EncuentroDeportivoEntity guardado = repository.saveAndFlush(aEntidad(encuentro));
        return aDominio(guardado);
    }

    @Override
    public void guardar(EncuentroDeportivo encuentro) {
        if (encuentro.getId() == null || !repository.existsById(encuentro.getId())) {
            throw new IllegalArgumentException("No existe el encuentro que se intenta actualizar");
        }
        repository.save(aEntidad(encuentro));
    }

    private EncuentroDeportivoEntity aEntidad(EncuentroDeportivo encuentro) {
        return new EncuentroDeportivoEntity(encuentro.getId(), encuentro.getDeporte(), encuentro.getFechaHora(),
                encuentro.getCancha(), encuentro.getCupoMinimo(), encuentro.getCupoMaximo(),
                encuentro.getOrganizador(), encuentro.getEstado(), encuentro.getParticipantes());
    }

    private EncuentroDeportivo aDominio(EncuentroDeportivoEntity entity) {
        return EncuentroDeportivo.restaurar(entity.getId(), entity.getDeporte(), entity.getFechaHora(),
                entity.getCancha(), entity.getCupoMinimo(), entity.getCupoMaximo(), entity.getOrganizador(),
                entity.getEstado(), entity.getParticipantes());
    }
}
