package poo.uniquindio.edu.co.Homa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.config.MetricsConfig.HomaBusinessMetrics;
import poo.uniquindio.edu.co.Homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.Homa.dto.response.ReservaResponse;
import poo.uniquindio.edu.co.Homa.exception.BusinessException;
import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.mapper.ReservaMapper;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Reserva;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoReserva;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.service.ReservaService;
import poo.uniquindio.edu.co.Homa.util.EmailService;

@Getter
@Setter
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;
    private final EmailService emailService;
    private final HomaBusinessMetrics metrics;

    @Override
    @Transactional
    public ReservaResponse crear(ReservaRequest request, Long clienteId) {
        log.info("Creando reserva - clienteId: {}, alojamientoId: {}, entrada: {}, salida: {}, huespedes: {}",
                clienteId, request.getAlojamientoId(), request.getFechaEntrada(),
                request.getFechaSalida(), request.getCantidadHuespedes());

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        Alojamiento alojamiento = alojamientoRepository.findById(request.getAlojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alojamiento no encontrado con id: " + request.getAlojamientoId()));

        // Validar que la fecha de salida sea posterior a la entrada
        if (!request.getFechaSalida().isAfter(request.getFechaEntrada())) {
            throw new BusinessException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        if (request.getCantidadHuespedes() != null
                && alojamiento.getMaxHuespedes() != null
                && request.getCantidadHuespedes() > alojamiento.getMaxHuespedes()) {
            throw new BusinessException("La cantidad de huespedes excede la capacidad del alojamiento");
        }

        // Verificar disponibilidad
        if (!verificarDisponibilidad(request.getAlojamientoId(), request.getFechaEntrada(), request.getFechaSalida())) {
            throw new BusinessException("El alojamiento no está disponible para las fechas seleccionadas");
        }

        // Calcular precio total con Double para precisión monetaria
        long dias = ChronoUnit.DAYS.between(request.getFechaEntrada(), request.getFechaSalida());
        Double precioTotal = alojamiento.getPrecioPorNoche() * dias;

        Reserva reserva = reservaMapper.toEntity(request);
        reserva.setHuesped(cliente);
        reserva.setAlojamiento(alojamiento);
        reserva.setPrecio(precioTotal);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setCreadoEn(LocalDateTime.now());

        reserva = reservaRepository.save(reserva);

        // ── Métricas de reserva ────────────────────────────────────────────────────
        metrics.incrementReservaCreada();
        metrics.registrarDuracionReserva(dias);
        metrics.registrarPrecioReserva(precioTotal);

        notificarAnfitrionNuevaReserva(reserva);

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
        if (!reserva.getHuesped().getId().equals(clienteId)) {
            throw new BusinessException("No tienes permiso para cancelar esta reserva");
        }

        // Verificar que la reserva pueda ser cancelada
        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.RECHAZADA) {
            throw new BusinessException("La reserva ya está cancelada");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new BusinessException("No se puede cancelar una reserva completada");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaCheckIn = reserva.getFechaEntrada().atStartOfDay();
        long horasHastaCheckIn = ChronoUnit.HOURS.between(ahora, fechaCheckIn);

        if (horasHastaCheckIn < 48) {
            throw new BusinessException("No puedes cancelar la reserva con menos de 48 horas de anticipacion");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        metrics.incrementReservaCancelada();
        notificarCambioEstado(reserva, EstadoReserva.CANCELADA);
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

        notificarCambioEstado(reserva, estado);
        log.info("Estado cambiado exitosamente para reserva: {}", reserva.getId());
    }

    @Override
    @Transactional
    public void cambiarEstadoVerificado(Long id, EstadoReserva estado, Long anfitrionId) {
        log.info("Cambiando estado de reserva {} a {} por anfitrion {}", id, estado, anfitrionId);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (!reserva.getAlojamiento().getAnfitrion().getId().equals(anfitrionId)) {
            throw new BusinessException("No tienes permiso para modificar esta reserva");
        }

        reserva.setEstado(estado);
        reservaRepository.save(reserva);

        notificarCambioEstado(reserva, estado);
        log.info("Estado cambiado exitosamente para reserva: {}", reserva.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        return reservaRepository.findByHuesped_Id(clienteId, pageable)
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
    public Page<ReservaResponse> listarPorAnfitrion(Long anfitrionId, Pageable pageable) {
        log.info("Buscando reservas para anfitrion ID: {}", anfitrionId);
        Page<Reserva> reservas = reservaRepository.findByAlojamiento_Anfitrion_Id(anfitrionId, pageable);
        log.info("Reservas encontradas: {}", reservas.getTotalElements());
        return reservas.map(reservaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long alojamientoId, LocalDate fechaInicio, LocalDate fechaFin) {
        metrics.incrementDisponibilidadConsulta();
        boolean disponible = reservaRepository.findReservasConflictivas(alojamientoId, fechaInicio, fechaFin).isEmpty();
        if (!disponible) {
            metrics.incrementDisponibilidadNoDisponible();
        }
        return disponible;
    }

    @Override
    @Transactional
    public void confirmarReserva(Long reservaId, Long anfitrionId) {
        log.info("Confirmando reserva {} por anfitrión {}", reservaId, anfitrionId);

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        // Verificar que el anfitrión sea el propietario del alojamiento
        if (!reserva.getAlojamiento().getAnfitrion().getId().equals(anfitrionId)) {
            throw new BusinessException("No tienes permiso para confirmar esta reserva");
        }

        // Verificar que la reserva esté en estado PENDIENTE_CONFIRMACION
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessException("Solo se pueden confirmar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        metrics.incrementReservaConfirmada();
        notificarCambioEstado(reserva, EstadoReserva.CONFIRMADA);
        log.info("Reserva confirmada exitosamente: {}", reserva.getId());
    }

    @Override
    @Transactional
    public void rechazarReserva(Long reservaId, Long anfitrionId) {
        log.info("Rechazando reserva {} por anfitrión {}", reservaId, anfitrionId);

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        // Verificar que el anfitrión sea el propietario del alojamiento
        if (!reserva.getAlojamiento().getAnfitrion().getId().equals(anfitrionId)) {
            throw new BusinessException("No tienes permiso para rechazar esta reserva");
        }

        // Verificar que la reserva esté en estado PENDIENTE_CONFIRMACION
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessException("Solo se pueden rechazar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.RECHAZADA);
        reservaRepository.save(reserva);

        metrics.incrementReservaRechazada();
        notificarCambioEstado(reserva, EstadoReserva.RECHAZADA);
        log.info("Reserva rechazada exitosamente: {}", reserva.getId());
    }

    @Override
    @Transactional
    public void completarReserva(Long reservaId, Long anfitrionId) {
        log.info("Completando reserva {} por anfitrión {}", reservaId, anfitrionId);

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        // Verificar que el anfitrión sea el propietario del alojamiento
        if (!reserva.getAlojamiento().getAnfitrion().getId().equals(anfitrionId)) {
            throw new BusinessException("No tienes permiso para completar esta reserva");
        }

        // Verificar que la reserva esté en estado CONFIRMADA
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new BusinessException("Solo se pueden completar reservas confirmadas");
        }

        reserva.setEstado(EstadoReserva.COMPLETADA);
        reservaRepository.save(reserva);

        metrics.incrementReservaCompletada();
        log.info("Reserva completada exitosamente: {}", reserva.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarReservasCompletadas(Long clienteId, Pageable pageable) {
        return reservaRepository.findByHuesped_IdAndEstado(clienteId, EstadoReserva.COMPLETADA, pageable)
                .map(reservaMapper::toResponse);
    }

    private void notificarAnfitrionNuevaReserva(Reserva reserva) {
        try {
            String emailAnfitrion = reserva.getAlojamiento().getAnfitrion().getEmail();
            String nombreHuesped = reserva.getHuesped().getNombre();
            String nombreAlojamiento = reserva.getAlojamiento().getTitulo();
            String rangoFechas = String.format("%s al %s", reserva.getFechaEntrada(), reserva.getFechaSalida());
            emailService.enviarEmailNuevaReservaAnfitrion(emailAnfitrion, nombreAlojamiento, nombreHuesped, rangoFechas);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de nueva reserva al anfitrion: {}", e.getMessage());
        }
    }

    private void notificarCambioEstado(Reserva reserva, EstadoReserva estado) {
        try {
            String emailHuesped = reserva.getHuesped().getEmail();
            String nombreAlojamiento = reserva.getAlojamiento().getTitulo();
            String rangoFechas = String.format("%s al %s", reserva.getFechaEntrada(), reserva.getFechaSalida());
            if (estado == EstadoReserva.CONFIRMADA) {
                emailService.enviarEmailConfirmacionReserva(emailHuesped, nombreAlojamiento, rangoFechas);
            } else if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.RECHAZADA) {
                emailService.enviarEmailCancelacionReserva(emailHuesped, nombreAlojamiento, rangoFechas);
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar email de cambio de estado: {}", e.getMessage());
        }
    }

}
