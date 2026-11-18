package com.sportsmatch;

import com.sportsmatch.application.handlers.CrearEncuentroHandler;
import com.sportsmatch.application.handlers.AbrirInscripcionesHandler;
import com.sportsmatch.application.handlers.InscribirParticipanteHandler;
import com.sportsmatch.application.handlers.ObtenerEncuentroPorIdHandler;
import com.sportsmatch.application.handlers.CerrarInscripcionesHandler;
import com.sportsmatch.application.handlers.CancelarEncuentroHandler;
import com.sportsmatch.application.handlers.ConfirmarEncuentroHandler;
import com.sportsmatch.application.handlers.FinalizarEncuentroHandler;
import com.sportsmatch.application.handlers.IniciarEncuentroHandler;
import com.sportsmatch.domain.model.EncuentroDeportivo;
import com.sportsmatch.domain.model.EstadoEncuentro;
import com.sportsmatch.application.ports.EncuentroDeportivoRepository;
import com.sportsmatch.presentation.web.CrearEncuentroRequest;
import com.sportsmatch.presentation.web.EncuentroDeportivoController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SportsMatchApplicationTests {

    private static final LocalDateTime FECHA_ENCUENTRO = LocalDateTime.of(2026, 10, 20, 18, 0);

    @Test
    void contextLoads() {
    }

    @Test
    void creaEncuentroEnBorradorConDatosValidos() {
        EncuentroDeportivo encuentro = crearEncuentro(1L);

        assertEquals("Fútbol", encuentro.getDeporte());
        assertEquals("Cancha Central", encuentro.getCancha());
        assertEquals(EstadoEncuentro.BORRADOR, encuentro.getEstado());
    }

    @Test
    void rechazaCuposInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> new EncuentroDeportivo(1L, "Fútbol", FECHA_ENCUENTRO, "Cancha Central", 12, 10, "Ana"));
    }

    @Test
    void completaElCicloDeVidaDelEncuentro() {
        EncuentroDeportivo encuentro = crearEncuentro(1L);

        encuentro.abrirInscripciones();
        encuentro.cerrarInscripciones();
        encuentro.confirmar();
        encuentro.iniciar();
        encuentro.finalizar();

        assertEquals(EstadoEncuentro.FINALIZADO, encuentro.getEstado());
    }

    @Test
    void noPermiteConfirmarUnEncuentroEnBorrador() {
        EncuentroDeportivo encuentro = crearEncuentro(3L);

        assertThrows(IllegalStateException.class, encuentro::confirmar);
    }

    @Test
    void inscribeParticipanteCuandoLasInscripcionesEstanAbiertas() {
        EncuentroDeportivo encuentro = crearEncuentro(5L);
        encuentro.abrirInscripciones();

        encuentro.inscribirParticipante("Luis");

        assertEquals(1, encuentro.getParticipantes().size());
        assertEquals("Luis", encuentro.getParticipantes().getFirst());
    }

    @Test
    void noPermiteInscribirAlMismoParticipanteDosVeces() {
        EncuentroDeportivo encuentro = crearEncuentro(6L);
        encuentro.abrirInscripciones();
        encuentro.inscribirParticipante("Luis");

        assertThrows(IllegalArgumentException.class,
                () -> encuentro.inscribirParticipante("Luis"));
    }

    @Test
    void creaYActualizaUnEncuentroMedianteLaApi() {
        TestEncuentroRepository repository = new TestEncuentroRepository();
        EncuentroDeportivoController controller = new EncuentroDeportivoController(
                new AbrirInscripcionesHandler(repository), new CerrarInscripcionesHandler(repository),
                new CancelarEncuentroHandler(repository),
                new ConfirmarEncuentroHandler(repository),
                new CrearEncuentroHandler(repository),
                new FinalizarEncuentroHandler(repository),
                new InscribirParticipanteHandler(repository),
                new IniciarEncuentroHandler(repository),
                new ObtenerEncuentroPorIdHandler(repository)
        );

        ResponseEntity<EncuentroDeportivo> respuesta = controller.crear(
                new CrearEncuentroRequest("Vóley", FECHA_ENCUENTRO, "Cancha Norte", 8, 12, "Carla")
        );
        Long generatedId = respuesta.getBody().getId();
        controller.abrirInscripciones(generatedId);
        controller.cerrarInscripciones(generatedId);
        controller.confirmar(generatedId);
        controller.iniciar(generatedId);
        controller.finalizar(generatedId);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(EstadoEncuentro.FINALIZADO, controller.obtener(generatedId).getEstado());

        Long encuentroCanceladoId = controller.crear(
                new CrearEncuentroRequest("Básquet", FECHA_ENCUENTRO, "Cancha Sur", 6, 10, "Diego")
        ).getBody().getId();
        assertEquals(EstadoEncuentro.CANCELADO, controller.cancelar(encuentroCanceladoId).getEstado());
    }

    private EncuentroDeportivo crearEncuentro(Long id) {
        return new EncuentroDeportivo(id, "Fútbol", FECHA_ENCUENTRO, "Cancha Central", 10, 14, "Ana");
    }

    private static class TestEncuentroRepository implements EncuentroDeportivoRepository {
        private final AtomicLong ids = new AtomicLong(1);
        private final Map<Long, EncuentroDeportivo> encuentros = new ConcurrentHashMap<>();

        @Override
        public Optional<EncuentroDeportivo> buscarPorId(Long id) {
            return Optional.ofNullable(encuentros.get(id));
        }

        @Override
        public EncuentroDeportivo guardarNuevo(EncuentroDeportivo encuentro) {
            EncuentroDeportivo asignado = EncuentroDeportivo.restaurar(ids.getAndIncrement(),
                    encuentro.getDeporte(), encuentro.getFechaHora(), encuentro.getCancha(),
                    encuentro.getCupoMinimo(), encuentro.getCupoMaximo(), encuentro.getOrganizador(),
                    encuentro.getEstado(), encuentro.getParticipantes());
            encuentros.put(asignado.getId(), asignado);
            return asignado;
        }

        @Override
        public void guardar(EncuentroDeportivo encuentro) {
            encuentros.put(encuentro.getId(), encuentro);
        }
    }
}
