package poo.uniquindio.edu.co.homa.service;


import co.edu.uniquindio.homa.dto.request.ResponderResenaRequest;
import co.edu.uniquindio.homa.dto.response.ResenaResponse;
import poo.uniquindio.edu.co.homa.dto.request.ResenaRequest;

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
