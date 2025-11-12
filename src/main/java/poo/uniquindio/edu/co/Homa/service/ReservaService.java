package poo.uniquindio.edu.co.homa.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poo.uniquindio.edu.co.homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.homa.dto.response.ReservaResponse;
import poo.uniquindio.edu.co.homa.model.enums.EstadoReserva;

import java.time.LocalDate;

public interface ReservaService {
    
    ReservaResponse crear(ReservaRequest request, Long clienteId);
    
    ReservaResponse obtenerPorId(Long id);
    
    void cancelar(Long id, Long clienteId);
    
    void cambiarEstado(Long id, EstadoReserva estado);
    
    Page<ReservaResponse> listarPorCliente(Long clienteId, Pageable pageable);

    Page<ReservaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable);

    Page<ReservaResponse> listarPorAnfitrion(Long anfitrionId, Pageable pageable);

    boolean verificarDisponibilidad(Long alojamientoId, LocalDate fechaInicio, LocalDate fechaFin);

    void confirmarReserva(Long reservaId, Long anfitrionId);

    void rechazarReserva(Long reservaId, Long anfitrionId);

    void completarReserva(Long reservaId, Long anfitrionId);

    Page<ReservaResponse> listarReservasCompletadas(Long clienteId, Pageable pageable);
}
