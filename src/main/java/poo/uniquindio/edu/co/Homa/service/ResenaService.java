package poo.uniquindio.edu.co.homa.service;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poo.uniquindio.edu.co.homa.dto.request.ResenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.ResponderResenaRequest;
import poo.uniquindio.edu.co.homa.dto.response.ResenaResponse;

public interface ResenaService {
    
    ResenaResponse crear(ResenaRequest request, Long clienteId);
    
    ResenaResponse obtenerPorId(Long id);
    
    void eliminar(Long id, Long clienteId);
    
    Page<ResenaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable);
    
    void responder(Long id, ResponderResenaRequest request, Long anfitrionId);

    Double calcularPromedioCalificacion(Long alojamientoId);

    Page<ResenaResponse> listarDestacadas(Pageable pageable);
}
