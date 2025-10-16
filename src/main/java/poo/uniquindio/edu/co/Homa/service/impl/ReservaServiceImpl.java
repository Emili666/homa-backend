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
        return reservaRepository.findByHuespedId(String.valueOf(clienteId), pageable)
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
        return reservaRepository.findReservasConflictivas(alojamientoId, fechaInicio, fechaFin).isEmpty();
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
