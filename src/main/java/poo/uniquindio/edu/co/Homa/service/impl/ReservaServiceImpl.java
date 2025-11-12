package poo.uniquindio.edu.co.homa.service.impl;

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
import poo.uniquindio.edu.co.homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.homa.dto.response.ReservaResponse;
import poo.uniquindio.edu.co.homa.exception.BusinessException;
import poo.uniquindio.edu.co.homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.homa.mapper.ReservaMapper;
import poo.uniquindio.edu.co.homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.homa.model.entity.Reserva;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoReserva;
import poo.uniquindio.edu.co.homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.ReservaService;
import poo.uniquindio.edu.co.homa.util.EmailService;

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

    @Override
    @Transactional
    public ReservaResponse crear(ReservaRequest request, Long clienteId) {
        log.info("Creando nueva reserva para cliente: {}", clienteId);

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        Alojamiento alojamiento = alojamientoRepository.findById(request.getAlojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alojamiento no encontrado con id: " + request.getAlojamientoId()));

        if (request.getCantidadHuespedes() != null
                && alojamiento.getMaxHuespedes() != null
                && request.getCantidadHuespedes() > alojamiento.getMaxHuespedes()) {
            throw new BusinessException("La cantidad de huespedes excede la capacidad del alojamiento");
        }

        // Verificar disponibilidad
        if (!verificarDisponibilidad(request.getAlojamientoId(), request.getFechaEntrada(), request.getFechaSalida())) {
            throw new BusinessException("El alojamiento no está disponible para las fechas seleccionadas");
        }

        // Calcular precio total
        long dias = ChronoUnit.DAYS.between(request.getFechaEntrada(), request.getFechaSalida());
        Double precioTotal = (double) (alojamiento.getPrecioPorNoche() * dias);

        Reserva reserva = reservaMapper.toEntity(request);
        reserva.setHuesped(cliente);
        reserva.setAlojamiento(alojamiento);
        reserva.setPrecio(precioTotal);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setCreadoEn(LocalDateTime.now());

        reserva = reservaRepository.save(reserva);

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
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
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
        System.out.println("========================================");
        System.out.println("=== LLAMANDO listarPorAnfitrion ===");
        System.out.println("Anfitrion ID: " + anfitrionId);
        System.out.println("Pageable: " + pageable);
        System.out.println("========================================");

        log.info("Buscando reservas para anfitrion ID: {}", anfitrionId);
        Page<Reserva> reservas = reservaRepository.findByAlojamiento_Anfitrion_Id(anfitrionId, pageable);
        log.info("Reservas encontradas: {}", reservas.getTotalElements());

        System.out.println("========================================");
        System.out.println("Total de reservas encontradas: " + reservas.getTotalElements());
        System.out.println("Numero de elementos en la pagina: " + reservas.getNumberOfElements());

        // Imprimir detalles de cada reserva encontrada
        if (reservas.hasContent()) {
            System.out.println("Detalles de las reservas:");
            reservas.getContent().forEach(reserva -> {
                System.out.println("  - ID Reserva: " + reserva.getId());
                System.out.println("    Alojamiento: " + reserva.getAlojamiento().getTitulo());
                System.out.println("    Huesped: " + reserva.getHuesped().getNombre());
                System.out.println("    Fecha entrada: " + reserva.getFechaEntrada());
                System.out.println("    Fecha salida: " + reserva.getFechaSalida());
                System.out.println("    Estado: " + reserva.getEstado());
                System.out.println("    ---");
            });
        } else {
            System.out.println("NO HAY RESERVAS PARA ESTE ANFITRION");
        }
        System.out.println("========================================");

        return reservas.map(reservaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long alojamientoId, LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findReservasConflictivas(alojamientoId, fechaInicio, fechaFin).isEmpty();
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

        // Verificar que la reserva esté en estado PENDIENTE
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessException("Solo se pueden confirmar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

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

        // Verificar que la reserva esté en estado PENDIENTE
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessException("Solo se pueden rechazar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        notificarCambioEstado(reserva, EstadoReserva.CANCELADA);
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

        log.info("Reserva completada exitosamente: {}", reserva.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarReservasCompletadas(Long clienteId, Pageable pageable) {
        return reservaRepository.findByHuesped_IdAndEstado(clienteId, EstadoReserva.COMPLETADA, pageable)
                .map(reservaMapper::toResponse);
    }

    private void notificarAnfitrionNuevaReserva(Reserva reserva) {
        String emailAnfitrion = reserva.getAlojamiento().getAnfitrion().getEmail();
        String nombreHuesped = reserva.getHuesped().getNombre();
        String nombreAlojamiento = reserva.getAlojamiento().getTitulo();
        String rangoFechas = String.format("%s al %s", reserva.getFechaEntrada(), reserva.getFechaSalida());

        emailService.enviarEmailNuevaReservaAnfitrion(emailAnfitrion, nombreAlojamiento, nombreHuesped, rangoFechas);
    }

    private void notificarCambioEstado(Reserva reserva, EstadoReserva estado) {
        String emailHuesped = reserva.getHuesped().getEmail();
        String nombreAlojamiento = reserva.getAlojamiento().getTitulo();
        String rangoFechas = String.format("%s al %s", reserva.getFechaEntrada(), reserva.getFechaSalida());

        if (estado == EstadoReserva.CONFIRMADA) {
            emailService.enviarEmailConfirmacionReserva(emailHuesped, nombreAlojamiento, rangoFechas);
        } else if (estado == EstadoReserva.CANCELADA) {
            emailService.enviarEmailCancelacionReserva(emailHuesped, nombreAlojamiento, rangoFechas);
        }
    }

}
