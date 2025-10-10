package poo.uniquindio.edu.co.homa.service.impl;

import co.edu.uniquindio.homa.dto.request.ResenaRequest;
import co.edu.uniquindio.homa.dto.request.ResponderResenaRequest;
import co.edu.uniquindio.homa.dto.response.ResenaResponse;
import co.edu.uniquindio.homa.exception.BusinessException;
import co.edu.uniquindio.homa.exception.ResourceNotFoundException;
import co.edu.uniquindio.homa.mapper.ResenaMapper;
import co.edu.uniquindio.homa.model.entity.Alojamiento;
import co.edu.uniquindio.homa.model.entity.Resena;
import co.edu.uniquindio.homa.model.entity.Usuario;
import co.edu.uniquindio.homa.repository.AlojamientoRepository;
import co.edu.uniquindio.homa.repository.ResenaRepository;
import co.edu.uniquindio.homa.repository.ReservaRepository;
import co.edu.uniquindio.homa.repository.UsuarioRepository;
import co.edu.uniquindio.homa.service.ResenaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final ResenaMapper resenaMapper;

    @Override
    @Transactional
    public ResenaResponse crear(ResenaRequest request, Long clienteId) {
        log.info("Creando nueva reseña para alojamiento: {}", request.getAlojamientoId());

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        Alojamiento alojamiento = alojamientoRepository.findById(request.getAlojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + request.getAlojamientoId()));

        // Verificar que el cliente haya tenido una reserva en el alojamiento
        boolean tieneReserva = reservaRepository.existsByClienteIdAndAlojamientoId(clienteId, request.getAlojamientoId());
        if (!tieneReserva) {
            throw new BusinessException("Solo puedes reseñar alojamientos donde hayas tenido una reserva");
        }

        // Verificar que no haya reseñado antes
        boolean yaReseno = resenaRepository.existsByClienteIdAndAlojamientoId(clienteId, request.getAlojamientoId());
        if (yaReseno) {
            throw new BusinessException("Ya has reseñado este alojamiento");
        }

        Resena resena = resenaMapper.toEntity(request);
        resena.setCliente(cliente);
        resena.setAlojamiento(alojamiento);
        resena.setFechaResena(LocalDateTime.now());

        resena = resenaRepository.save(resena);

        log.info("Reseña creada exitosamente con id: {}", resena.getId());
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
        if (!resena.getCliente().getId().equals(clienteId)) {
            throw new BusinessException("No tienes permiso para eliminar esta reseña");
        }

        resenaRepository.delete(resena);

        log.info("Reseña eliminada exitosamente: {}", resena.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResenaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable) {
        return resenaRepository.findByAlojamientoId(alojamientoId, pageable)
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

        resena.setRespuestaAnfitrion(request.getRespuesta());
        resena.setFechaRespuesta(LocalDateTime.now());
        resenaRepository.save(resena);

        log.info("Respuesta agregada exitosamente a la reseña: {}", resena.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularPromedioCalificacion(Long alojamientoId) {
        return resenaRepository.calcularPromedioCalificacion(alojamientoId);
    }
}
