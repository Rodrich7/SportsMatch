package com.sportsmatch.presentation.web;

import com.sportsmatch.application.commands.AbrirInscripcionesCommand;
import com.sportsmatch.application.commands.CancelarEncuentroCommand;
import com.sportsmatch.application.commands.CerrarInscripcionesCommand;
import com.sportsmatch.application.commands.ConfirmarEncuentroCommand;
import com.sportsmatch.application.commands.CrearEncuentroCommand;
import com.sportsmatch.application.commands.FinalizarEncuentroCommand;
import com.sportsmatch.application.commands.InscribirParticipanteCommand;
import com.sportsmatch.application.commands.IniciarEncuentroCommand;
import com.sportsmatch.application.handlers.CerrarInscripcionesHandler;
import com.sportsmatch.application.handlers.CancelarEncuentroHandler;
import com.sportsmatch.application.handlers.ConfirmarEncuentroHandler;
import com.sportsmatch.application.handlers.CrearEncuentroHandler;
import com.sportsmatch.application.handlers.FinalizarEncuentroHandler;
import com.sportsmatch.application.handlers.AbrirInscripcionesHandler;
import com.sportsmatch.application.handlers.InscribirParticipanteHandler;
import com.sportsmatch.application.handlers.IniciarEncuentroHandler;
import com.sportsmatch.application.handlers.ObtenerEncuentroPorIdHandler;
import com.sportsmatch.application.queries.ObtenerEncuentroPorIdQuery;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import com.sportsmatch.presentation.web.assembler.EncuentroDeportivoAssembler;
import com.sportsmatch.presentation.web.dto.EncuentroDeportivoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/encuentros")
public class EncuentroDeportivoController {

    private final AbrirInscripcionesHandler abrirInscripcionesHandler;
    private final CerrarInscripcionesHandler cerrarInscripcionesHandler;
    private final CancelarEncuentroHandler cancelarEncuentroHandler;
    private final ConfirmarEncuentroHandler confirmarEncuentroHandler;
    private final CrearEncuentroHandler crearEncuentroHandler;
    private final FinalizarEncuentroHandler finalizarEncuentroHandler;
    private final InscribirParticipanteHandler inscribirParticipanteHandler;
    private final IniciarEncuentroHandler iniciarEncuentroHandler;
    private final ObtenerEncuentroPorIdHandler obtenerEncuentroPorIdHandler;
    private final EncuentroDeportivoAssembler encuentroAssembler;

    public EncuentroDeportivoController(AbrirInscripcionesHandler abrirInscripcionesHandler,
                                        CerrarInscripcionesHandler cerrarInscripcionesHandler,
                                        CancelarEncuentroHandler cancelarEncuentroHandler,
                                        ConfirmarEncuentroHandler confirmarEncuentroHandler,
                                        CrearEncuentroHandler crearEncuentroHandler,
                                        FinalizarEncuentroHandler finalizarEncuentroHandler,
                                        InscribirParticipanteHandler inscribirParticipanteHandler,
                                        IniciarEncuentroHandler iniciarEncuentroHandler,
                                        ObtenerEncuentroPorIdHandler obtenerEncuentroPorIdHandler,
                                        EncuentroDeportivoAssembler encuentroAssembler) {
        this.abrirInscripcionesHandler = abrirInscripcionesHandler;
        this.cerrarInscripcionesHandler = cerrarInscripcionesHandler;
        this.cancelarEncuentroHandler = cancelarEncuentroHandler;
        this.confirmarEncuentroHandler = confirmarEncuentroHandler;
        this.crearEncuentroHandler = crearEncuentroHandler;
        this.finalizarEncuentroHandler = finalizarEncuentroHandler;
        this.inscribirParticipanteHandler = inscribirParticipanteHandler;
        this.iniciarEncuentroHandler = iniciarEncuentroHandler;
        this.obtenerEncuentroPorIdHandler = obtenerEncuentroPorIdHandler;
        this.encuentroAssembler = encuentroAssembler;
    }

    @PostMapping
    public ResponseEntity<EncuentroDeportivoResponse> crear(@Valid @RequestBody CrearEncuentroRequest solicitud) {
        EncuentroDeportivo encuentro = crearEncuentroHandler.manejar(new CrearEncuentroCommand(
                solicitud.deporte(), solicitud.fechaHora(), solicitud.cancha(),
                solicitud.cupoMinimo(), solicitud.cupoMaximo(), solicitud.organizador()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(encuentroAssembler.aRespuesta(encuentro));
    }

    @GetMapping("/{id}")
    public EncuentroDeportivoResponse obtener(@PathVariable Long id) {
        return encuentroAssembler.aRespuesta(buscarEncuentro(id));
    }

    @PostMapping("/{id}/abrir-inscripciones")
    public EncuentroDeportivoResponse abrirInscripciones(@PathVariable Long id) {
        return abrirInscripcionesHandler.manejar(new AbrirInscripcionesCommand(id))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @PostMapping("/{id}/cerrar-inscripciones")
    public EncuentroDeportivoResponse cerrarInscripciones(@PathVariable Long id) {
        return cerrarInscripcionesHandler.manejar(new CerrarInscripcionesCommand(id))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @PostMapping("/{id}/confirmar")
    public EncuentroDeportivoResponse confirmar(@PathVariable Long id) {
        return confirmarEncuentroHandler.manejar(new ConfirmarEncuentroCommand(id))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @PostMapping("/{id}/iniciar")
    public EncuentroDeportivoResponse iniciar(@PathVariable Long id) {
        return iniciarEncuentroHandler.manejar(new IniciarEncuentroCommand(id))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @PostMapping("/{id}/finalizar")
    public EncuentroDeportivoResponse finalizar(@PathVariable Long id) {
        return finalizarEncuentroHandler.manejar(new FinalizarEncuentroCommand(id))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @PostMapping("/{id}/cancelar")
    public EncuentroDeportivoResponse cancelar(@PathVariable Long id) {
        return cancelarEncuentroHandler.manejar(new CancelarEncuentroCommand(id))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @PostMapping("/{id}/participantes")
    public EncuentroDeportivoResponse inscribirParticipante(@PathVariable Long id,
                                                            @RequestBody InscribirParticipanteRequest solicitud) {
        return inscribirParticipanteHandler.manejar(
                        new InscribirParticipanteCommand(id, solicitud.participante()))
                .map(encuentroAssembler::aRespuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarReglaDeNegocio(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarSolicitudInvalida(MethodArgumentNotValidException exception) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Los datos del encuentro son obligatorios y deben ser válidos");
        return ResponseEntity.badRequest().body(Map.of("error", mensaje));
    }

    private EncuentroDeportivo buscarEncuentro(Long id) {
        return obtenerEncuentroPorIdHandler.manejar(new ObtenerEncuentroPorIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuentro no encontrado"));
    }
}
