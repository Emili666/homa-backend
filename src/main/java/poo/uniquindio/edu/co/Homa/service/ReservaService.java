package co.edu.uniquindio.homa.service;

import co.edu.uniquindio.homa.dto.request.ReservaRequest;
import co.edu.uniquindio.homa.dto.response.ReservaResponse;
import co.edu.uniquindio.homa.model.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservaService {
    
    ReservaResponse crear(ReservaRequest request, Long clienteId);
    
    ReservaResponse obtenerPorId(Long id);
    
    void cancelar(Long id, Long clienteId);
    
    void cambiarEstado(Long id, EstadoReserva estado);
    
    Page<ReservaResponse> listarPorCliente(Long clienteId, Pageable pageable);
    
    Page<ReservaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable);
    
    boolean verificarDisponibilidad(Long alojamientoId, LocalDate fechaInicio, LocalDate fechaFin);
}
