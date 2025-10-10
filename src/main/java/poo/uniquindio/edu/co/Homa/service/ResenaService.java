package co.edu.uniquindio.homa.service;

import co.edu.uniquindio.homa.dto.request.ResenaRequest;
import co.edu.uniquindio.homa.dto.request.ResponderResenaRequest;
import co.edu.uniquindio.homa.dto.response.ResenaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResenaService {
    
    ResenaResponse crear(ResenaRequest request, Long clienteId);
    
    ResenaResponse obtenerPorId(Long id);
    
    void eliminar(Long id, Long clienteId);
    
    Page<ResenaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable);
    
    void responder(Long id, ResponderResenaRequest request, Long anfitrionId);
    
    Double calcularPromedioCalificacion(Long alojamientoId);
}
