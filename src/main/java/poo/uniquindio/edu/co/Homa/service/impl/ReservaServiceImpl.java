package co.edu.uniquindio.homa.service.impl;

import co.edu.uniquindio.homa.dto.request.ReservaRequest;
import co.edu.uniquindio.homa.dto.response.ReservaResponse;
import co.edu.uniquindio.homa.exception.BusinessException;
import co.edu.uniquindio.homa.exception.ResourceNotFoundException;
import co.edu.uniquindio.homa.mapper.ReservaMapper;
import co.edu.uniquindio.homa.model.entity.Alojamiento;
import co.edu.uniquindio.homa.model.entity.Reserva;
import co.edu.uniquindio.homa.model.entity.Usuario;
import co.edu.uniquindio.homa.model.enums.EstadoReserva;
import co.edu.uniquindio.homa.repository.AlojamientoRepository;
import co.edu.uniquindio.homa.repository.ReservaRepository;
import co.edu.uniquindio.homa.repository.UsuarioRepository;
import co.edu.uniquindio.homa.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;

    @Override
    @Transactional
    public ReservaResponse crear(ReservaRequest request, Long clienteId) {
        log.info("Creando nueva reserva para cliente: {}", clienteId);

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        Alojamiento alojamiento = alojamientoRepository.findById(request.getAlojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + request.getAlojamientoId()));

        // Verificar disponibilidad
        if (!verificarDisponibilidad(request.getAlojamientoId(), request.getFechaInicio(), request.getFechaFin())) {
            throw new BusinessException("El alojamiento no está disponible para las fechas seleccionadas");
        }

        // Calcular precio total
        long dias = ChronoUnit.DAYS.between(request.getFechaInicio(), request.getFechaFin());
        BigDecimal precioTotal = alojamiento.getPrecioPorNoche().multiply(BigDecimal.valueOf(dias));

        Reserva reserva = reservaMapper.toEntity(request);
        reserva.setCliente(cliente);
        reserva.setAlojamiento(alojamiento);
        reserva.setPrecioTotal(precioTotal);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());

        reserva = reservaRepository.save(reserva);

        log.info("Reserva creada exitosamente con id: {}", reserva.getId());
        return reservaMapper.toResponse(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        return reservaMapper.toResponse(reserva);
    }

    @Override
    @Transactional
    public void cancelar(Long id, Long clienteId) {
        log.info("Cancelando reserva con id: {}", id);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        // Verificar que el cliente sea el propietario
        if (!reserva.getCliente().getId().equals(clienteId)) {
            throw new BusinessException("No tienes permiso para cancelar esta reserva");
        }

        // Verificar que la reserva pueda ser cancelada
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BusinessException("La reserva ya está cancelada");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new BusinessException("No se puede cancelar una reserva completada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        log.info("Reserva cancelada exitosamente: {}", reserva.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, EstadoReserva estado) {
        log.info("Cambiando estado de reserva {} a {}", id, estado);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        reserva.setEstado(estado);
        reservaRepository.save(reserva);

        log.info("Estado cambiado exitosamente para reserva: {}", reserva.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        return reservaRepository.findByClienteId(clienteId, pageable)
                .map(reservaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarPorAlojamiento(Long alojamientoId, Pageable pageable) {
        return reservaRepository.findByAlojamientoId(alojamientoId, pageable)
                .map(reservaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long alojamientoId, LocalDate fechaInicio, LocalDate fechaFin) {
        return !reservaRepository.existsReservaEnFechas(alojamientoId, fechaInicio, fechaFin);
    }
}
