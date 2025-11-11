package poo.uniquindio.edu.co.homa.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.homa.dto.request.ResenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.ResponderResenaRequest;
import poo.uniquindio.edu.co.homa.dto.response.ResenaResponse;
import poo.uniquindio.edu.co.homa.exception.BusinessException;
import poo.uniquindio.edu.co.homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.homa.mapper.ResenaMapper;
import poo.uniquindio.edu.co.homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.homa.model.entity.Resena;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoReserva;
import poo.uniquindio.edu.co.homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.homa.repository.ResenaRepository;
import poo.uniquindio.edu.co.homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.ResenaService;
import poo.uniquindio.edu.co.homa.util.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final ResenaMapper resenaMapper;
    private final EmailService emailService;

   @Override
@Transactional
public ResenaResponse crear(ResenaRequest request, Long clienteId) {
    log.info("Creando nueva reseña para alojamiento con ID: {}", request.getAlojamientoId());

    // Buscar cliente y alojamiento
    Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

    Alojamiento alojamiento = alojamientoRepository.findById(request.getAlojamientoId())
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Alojamiento no encontrado con ID: " + request.getAlojamientoId()));

    // Verificar que el cliente tenga una reserva confirmada en el alojamiento
    boolean tieneReserva = reservaRepository.existsByHuesped_IdAndAlojamiento_IdAndEstado(
            clienteId, // ← ahora se pasa como Long (no como String)
            request.getAlojamientoId(),
            EstadoReserva.COMPLETADA
            );

    if (!tieneReserva) {
        throw new BusinessException("Solo puedes reseñar alojamientos donde hayas tenido una reserva completada.");
    }

    // Verificar que no haya reseñado antes
    boolean yaReseno = resenaRepository.existsByUsuarioIdAndAlojamientoId(
            clienteId, // ← también se pasa como Long
            request.getAlojamientoId());

    if (yaReseno) {
        throw new BusinessException("Ya has realizado una reseña para este alojamiento.");
    }

    // Crear y guardar la reseña
    Resena resena = resenaMapper.toEntity(request);
    resena.setUsuario(cliente);
    resena.setAlojamiento(alojamiento);

    resena = resenaRepository.save(resena);

    notificarAnfitrionNuevaResena(alojamiento, cliente, request);

    log.info("Reseña creada exitosamente con ID: {}", resena.getId());
    return resenaMapper.toResponse(resena);
}


    @Override
    @Transactional(readOnly = true)
    public ResenaResponse obtenerPorId(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));
        return resenaMapper.toResponse(resena);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Long clienteId) {
        log.info("Eliminando reseña con id: {}", id);

        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));

        // Verificar que el cliente sea el propietario
        if (!resena.getUsuario().getId().equals(clienteId)) {
            throw new BusinessException("No tienes permiso para eliminar esta reseña");
        }

        resenaRepository.delete(resena);

        log.info("Reseña eliminada exitosamente: {}", resena.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResenaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable) {
        return resenaRepository.findByAlojamientoIdOrderByCreadoEnDesc(alojamientoId, pageable)
                .map(resenaMapper::toResponse);
    }

    @Override
    @Transactional
    public void responder(Long id, ResponderResenaRequest request, Long anfitrionId) {
        log.info("Respondiendo reseña con id: {}", id);

        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));

        // Verificar que el anfitrión sea el propietario del alojamiento
        if (!resena.getAlojamiento().getAnfitrion().getId().equals(anfitrionId)) {
            throw new BusinessException("No tienes permiso para responder esta reseña");
        }

        resena.setComentario(request.getMensaje());
        resena.setRespondidoEn(LocalDateTime.now());

        resenaRepository.save(resena);

        log.info("Respuesta agregada exitosamente a la reseña: {}", resena.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularPromedioCalificacion(Long alojamientoId) {
        return resenaRepository.calcularPromedioCalificacion(alojamientoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResenaResponse> listarDestacadas(Pageable pageable) {
        log.info("Listando reseñas destacadas");
        // Primero intentar obtener las marcadas como destacadas
        Page<Resena> destacadas = resenaRepository.findDestacadas(pageable);

        // Si no hay destacadas, obtener las mejores calificadas (4-5 estrellas)
        if (destacadas.isEmpty()) {
            log.info("No hay reseñas destacadas, obteniendo mejor calificadas");
            destacadas = resenaRepository.findByCalificacionGreaterThanEqualOrderByCreadoEnDesc(4, pageable);
        }

        return destacadas.map(resenaMapper::toResponse);
    }

    private void notificarAnfitrionNuevaResena(Alojamiento alojamiento, Usuario huesped, ResenaRequest request) {
        if (alojamiento.getAnfitrion() == null || alojamiento.getAnfitrion().getEmail() == null) {
            log.warn("No se pudo notificar al anfitrion: alojamiento {} sin anfitrion o sin email", alojamiento.getId());
            return;
        }

        emailService.enviarEmailNuevaResenaAnfitrion(
                alojamiento.getAnfitrion().getEmail(),
                alojamiento.getTitulo(),
                huesped.getNombre(),
                request.getCalificacion(),
                request.getComentario()
        );
    }

}
