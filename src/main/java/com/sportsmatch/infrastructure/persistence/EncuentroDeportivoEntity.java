package com.sportsmatch.infrastructure.persistence;

import com.sportsmatch.domain.model.EstadoEncuentro;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "encuentros_deportivos")
public class EncuentroDeportivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deporte;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String cancha;

    @Column(name = "cupo_minimo", nullable = false)
    private int cupoMinimo;

    @Column(name = "cupo_maximo", nullable = false)
    private int cupoMaximo;

    @Column(nullable = false)
    private String organizador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEncuentro estado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "encuentro_participantes", joinColumns = @JoinColumn(name = "encuentro_id"))
    @Column(name = "participante", nullable = false)
    private List<String> participantes = new ArrayList<>();

    protected EncuentroDeportivoEntity() {
    }

    EncuentroDeportivoEntity(Long id, String deporte, LocalDateTime fechaHora, String cancha,
                             int cupoMinimo, int cupoMaximo, String organizador, EstadoEncuentro estado,
                             List<String> participantes) {
        this.id = id;
        this.deporte = deporte;
        this.fechaHora = fechaHora;
        this.cancha = cancha;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.organizador = organizador;
        this.estado = estado;
        this.participantes = new ArrayList<>(participantes);
    }

    Long getId() { return id; }
    String getDeporte() { return deporte; }
    LocalDateTime getFechaHora() { return fechaHora; }
    String getCancha() { return cancha; }
    int getCupoMinimo() { return cupoMinimo; }
    int getCupoMaximo() { return cupoMaximo; }
    String getOrganizador() { return organizador; }
    EstadoEncuentro getEstado() { return estado; }
    List<String> getParticipantes() { return participantes; }
}
