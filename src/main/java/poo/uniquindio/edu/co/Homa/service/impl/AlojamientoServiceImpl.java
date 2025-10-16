package poo.uniquindio.edu.co.homa.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.homa.dto.request.AlojamientoRequest;
import poo.uniquindio.edu.co.homa.dto.response.AlojamientoResponse;
import poo.uniquindio.edu.co.homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.homa.mapper.AlojamientoMapper;
import poo.uniquindio.edu.co.homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.homa.repository.FavoritoRepository;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.AlojamientoService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlojamientoServiceImpl implements AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoMapper alojamientoMapper;
    private final FavoritoRepository favoritoRepository;

    @Override
    @Transactional
    public AlojamientoResponse crear(AlojamientoRequest request, Long anfitrionId) {
        log.info("Creando nuevo alojamiento para anfitrión: {}", anfitrionId);

        Usuario anfitrion = usuarioRepository.findById(anfitrionId)
                .orElseThrow(() -> new ResourceNotFoundException("Anfitrión no encontrado con id: " + anfitrionId));

        Alojamiento alojamiento = alojamientoMapper.toEntity(request);
        alojamiento.setAnfitrion(anfitrion);
        alojamiento.setEstado(EstadoAlojamiento.PENDIENTE);
        alojamiento.setCreadoEn(LocalDateTime.now());

        alojamiento = alojamientoRepository.save(alojamiento);

        log.info("Alojamiento creado exitosamente con id: {}", alojamiento.getId());
        return mapearConMetadatos(alojamiento);
    }

    @Override
    @Transactional(readOnly = true)
    public AlojamientoResponse obtenerPorId(Long id) {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));
        return mapearConMetadatos(alojamiento);
    }

    @Override
    @Transactional
    public AlojamientoResponse actualizar(Long id, AlojamientoRequest request, Long anfitrionId) {
        log.info("Actualizando alojamiento con id: {}", id);

        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        // Verificar que el anfitrión sea el propietario
        if (!alojamiento.getAnfitrion().getId().equals(anfitrionId)) {
            throw new UnauthorizedException("No tienes permiso para actualizar este alojamiento");
        }

        alojamientoMapper.updateEntityFromRequest(request, alojamiento);
        alojamiento = alojamientoRepository.save(alojamiento);

        log.info("Alojamiento actualizado exitosamente: {}", alojamiento.getId());
        return mapearConMetadatos(alojamiento);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Long anfitrionId) {
        log.info("Eliminando alojamiento con id: {}", id);

        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        // Verificar que el anfitrión sea el propietario
        if (!alojamiento.getAnfitrion().getId().equals(anfitrionId)) {
            throw new UnauthorizedException("No tienes permiso para eliminar este alojamiento");
        }

        alojamiento.setEstado(EstadoAlojamiento.ELIMINADO);
        alojamientoRepository.save(alojamiento);

        log.info("Alojamiento eliminado exitosamente: {}", alojamiento.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlojamientoResponse> listarTodos(Pageable pageable) {
        return alojamientoRepository.findByEstado(EstadoAlojamiento.ACTIVO, pageable)
                .map(this::mapearConMetadatos);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlojamientoResponse> listarPorAnfitrion(Long anfitrionId, Pageable pageable) {
        return alojamientoRepository.findByAnfitrionId(anfitrionId, pageable)
                .map(this::mapearConMetadatos);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlojamientoResponse> buscar(String ciudad, BigDecimal precioMin, BigDecimal precioMax,
            Integer capacidad, Pageable pageable) {

        Float precioMinF = precioMin != null ? precioMin.floatValue() : null;
        Float precioMaxF = precioMax != null ? precioMax.floatValue() : null;

        return alojamientoRepository.buscarAlojamientos(
                EstadoAlojamiento.ACTIVO,
                ciudad,
                precioMinF,
                precioMaxF,
                capacidad,
                pageable)
                .map(this::mapearConMetadatos);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, EstadoAlojamiento estado) {
        log.info("Cambiando estado de alojamiento {} a {}", id, estado);

        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        alojamiento.setEstado(estado);
        alojamientoRepository.save(alojamiento);

        log.info("Estado cambiado exitosamente para alojamiento: {}", alojamiento.getId());
    }

    @Override
    @Transactional
    public void agregarImagenes(Long id, List<MultipartFile> imagenes) {
        // Implementación de carga de imágenes
        log.info("Agregando {} imágenes al alojamiento {}", imagenes.size(), id);
        // TODO: Implementar lógica de almacenamiento de imágenes
    }

    @Override
    @Transactional
    public void eliminarImagen(Long alojamientoId, Long imagenId) {
        log.info("Eliminando imagen {} del alojamiento {}", imagenId, alojamientoId);
        // TODO: Implementar lógica de eliminación de imágenes
    }

    private AlojamientoResponse mapearConMetadatos(Alojamiento alojamiento) {
        AlojamientoResponse response = alojamientoMapper.toResponse(alojamiento);
        response.setTotalFavoritos(favoritoRepository.countByAlojamientoId(alojamiento.getId()));
        if (response.getEsFavorito() == null) {
            response.setEsFavorito(Boolean.FALSE);
        }
        return response;
    }
}


