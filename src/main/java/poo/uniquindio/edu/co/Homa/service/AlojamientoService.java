package co.edu.uniquindio.homa.service;

import co.edu.uniquindio.homa.dto.request.AlojamientoRequest;
import co.edu.uniquindio.homa.dto.response.AlojamientoResponse;
import co.edu.uniquindio.homa.model.enums.EstadoAlojamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface AlojamientoService {
    
    AlojamientoResponse crear(AlojamientoRequest request, Long anfitrionId);
    
    AlojamientoResponse obtenerPorId(Long id);
    
    AlojamientoResponse actualizar(Long id, AlojamientoRequest request, Long anfitrionId);
    
    void eliminar(Long id, Long anfitrionId);
    
    Page<AlojamientoResponse> listarTodos(Pageable pageable);
    
    Page<AlojamientoResponse> listarPorAnfitrion(Long anfitrionId, Pageable pageable);
    
    Page<AlojamientoResponse> buscar(String ciudad, BigDecimal precioMin, BigDecimal precioMax, 
                                     Integer capacidad, Pageable pageable);
    
    void cambiarEstado(Long id, EstadoAlojamiento estado);
    
    void agregarImagenes(Long id, List<MultipartFile> imagenes);
    
    void eliminarImagen(Long alojamientoId, Long imagenId);
}
