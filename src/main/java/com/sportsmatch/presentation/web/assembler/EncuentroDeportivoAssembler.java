package com.sportsmatch.presentation.web.assembler;

import com.sportsmatch.domain.model.EncuentroDeportivo;
import com.sportsmatch.presentation.web.dto.EncuentroDeportivoResponse;
import org.springframework.stereotype.Component;

@Component
public class EncuentroDeportivoAssembler {

    public EncuentroDeportivoResponse aRespuesta(EncuentroDeportivo encuentro) {
        return new EncuentroDeportivoResponse(
                encuentro.getId(),
                encuentro.getDeporte(),
                encuentro.getFechaHora(),
                encuentro.getCancha(),
                encuentro.getCupoMinimo(),
                encuentro.getCupoMaximo(),
                encuentro.getOrganizador(),
                encuentro.getEstado(),
                encuentro.getParticipantes(),
                encuentro.getResultado()
        );
    }
}
