package poo.uniquindio.edu.co.Homa.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.dto.request.AlojamientoRequest;
import poo.uniquindio.edu.co.Homa.dto.response.AlojamientoResponse;
import poo.uniquindio.edu.co.Homa.exception.BusinessException;
import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.Homa.mapper.AlojamientoMapper;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.repository.FavoritoRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.service.AlojamientoService;
import poo.uniquindio.edu.co.Homa.service.ImageStorageService;
import poo.uniquindio.edu.co.Homa.service.ImageStorageService.UploadResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlojamientoServiceImpl implements AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoMapper alojamientoMapper;
    private final FavoritoRepository favoritoRepository;
    private final ImageStorageService imageStorageService;

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
        // Verificar si el alojamiento tiene reservas futuras
        if (alojamientoRepository.tieneReservasFuturas(id)) {
            throw new RuntimeException("No se puede eliminar el alojamiento porque tiene reservas futuras");
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
        log.info("Agregando {} imagenes al alojamiento {}", imagenes != null ? imagenes.size() : 0, id);

        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        if (imagenes == null || imagenes.isEmpty()) {
            throw new BusinessException("Debes adjuntar al menos una imagen");
        }

        List<MultipartFile> archivosValidos = imagenes.stream()
                .filter(Objects::nonNull)
                .filter(archivo -> !archivo.isEmpty())
                .toList();

        if (archivosValidos.isEmpty()) {
            throw new BusinessException("Todas las imagenes adjuntas estan vacias");
        }

        int espacioDisponible = 10 - alojamiento.getImagenes().size();
        if (archivosValidos.size() > espacioDisponible) {
            throw new BusinessException("Solo puedes almacenar hasta 10 imagenes por alojamiento");
        }

        List<UploadResult> resultados = imageStorageService.subirImagenes(archivosValidos);
        resultados.forEach(resultado -> alojamiento.getImagenes().add(resultado.url()));

        alojamientoRepository.save(alojamiento);
        log.info("Se agregaron {} imagenes al alojamiento {}", resultados.size(), id);
    }

    @Override
    @Transactional
    public void eliminarImagen(Long alojamientoId, Long imagenId) {
        log.info("Eliminando imagen {} del alojamiento {}", imagenId, alojamientoId);

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + alojamientoId));

        if (imagenId == null || imagenId < 0 || imagenId >= alojamiento.getImagenes().size()) {
            throw new ResourceNotFoundException("Imagen no encontrada para el alojamiento indicado");
        }

        int index = Math.toIntExact(imagenId);
        String urlImagen = alojamiento.getImagenes().get(index);
        String publicId = extraerPublicIdDesdeUrl(urlImagen);

        imageStorageService.eliminarImagen(publicId);
        alojamiento.getImagenes().remove(index);
        alojamientoRepository.save(alojamiento);

        log.info("Imagen eliminada correctamente del alojamiento {}", alojamientoId);
    }

    private AlojamientoResponse mapearConMetadatos(Alojamiento alojamiento) {
        AlojamientoResponse response = alojamientoMapper.toResponse(alojamiento);
        response.setTotalFavoritos(favoritoRepository.countByAlojamientoId(alojamiento.getId()));
        if (response.getEsFavorito() == null) {
            response.setEsFavorito(Boolean.FALSE);
        }
        return response;
    }

    private String extraerPublicIdDesdeUrl(String urlImagen) {
        if (urlImagen == null || urlImagen.isBlank()) {
            throw new BusinessException("La URL de la imagen es invalida");
        }

        int uploadIndex = urlImagen.indexOf("/upload/");
        if (uploadIndex == -1) {
            throw new BusinessException("No se pudo extraer el identificador de la imagen");
        }

        String path = urlImagen.substring(uploadIndex + "/upload/".length());
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            path = path.substring(0, queryIndex);
        }

        int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex != -1) {
            path = path.substring(0, extensionIndex);
        }

        return path;
    }
}
