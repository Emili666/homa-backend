package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dtos.alojamiento.ItemAlojamientoDTO;
import co.edu.uniquindio.application.dtos.reserva.CreacionReservaDTO;
import co.edu.uniquindio.application.dtos.reserva.ItemReservaDTO;
import co.edu.uniquindio.application.dtos.reserva.ReservaDTO;
import co.edu.uniquindio.application.models.entitys.Alojamiento;
import co.edu.uniquindio.application.models.entitys.Reserva;
import co.edu.uniquindio.application.models.entitys.Usuario;
import co.edu.uniquindio.application.models.enums.ReservaEstado;
import co.edu.uniquindio.application.repositories.AlojamientoRepositorio;
import co.edu.uniquindio.application.repositories.ReservaRepositorio;
import co.edu.uniquindio.application.repositories.UsuarioRepositorio;
import co.edu.uniquindio.application.services.ReservaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaServicioImpl implements ReservaServicio {

    private final ReservaRepositorio reservaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final AlojamientoRepositorio alojamientoRepositorio;

    @Override
    public ReservaDTO crearReserva(CreacionReservaDTO dto) throws Exception {
        // Obtener usuario autenticado del SecurityContext
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usuarioId = userDetails.getUsername();

        Usuario usuario = usuarioRepositorio.findById(usuarioId)
            .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoRepositorio.findById(dto.alojamientoId())
            .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        // Validar que fechaSalida sea posterior a fechaEntrada
        if (!dto.fechaSalida().isAfter(dto.fechaEntrada())) {
            throw new Exception("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        // Calcular precio total
        long dias = ChronoUnit.DAYS.between(dto.fechaEntrada(), dto.fechaSalida());
        double precioTotal = alojamiento.getPrecioPorNoche() * dias;

        // Crear la reserva
        Reserva reserva = Reserva.builder()
            .alojamiento(alojamiento)
            .huesped(usuario)
            .fechaEntrada(dto.fechaEntrada())
            .fechaSalida(dto.fechaSalida())
            .cantidadHuespedes(dto.numeroHuespedes())
            .precio(precioTotal)
            .estado(ReservaEstado.PENDIENTE)
            .creadoEn(LocalDateTime.now())
            .build();

        reserva = reservaRepositorio.save(reserva);

        // TODO: Implement proper ReservaDTO with AlojamientoDTO and UsuarioDTO
        return null; // Temporarily return null until DTO is properly implemented
    }

    @Override
    public Page<ItemReservaDTO> listarReservas(String estado, String fechaInicio, String fechaFin, Pageable pageable) throws Exception {
        // Obtener usuario autenticado
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usuarioId = userDetails.getUsername();

        Usuario usuario = usuarioRepositorio.findById(usuarioId)
            .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Obtener reservas del usuario
        Page<Reserva> reservasPage = reservaRepositorio.findByHuespedId(usuarioId, pageable);

        // Convertir a ItemReservaDTO
        List<ItemReservaDTO> items = reservasPage.getContent().stream()
            .map(reserva -> new ItemReservaDTO(
                reserva.getId(),
                reserva.getAlojamiento().getId(),
                reserva.getAlojamiento().getTitulo(),
                reserva.getAlojamiento().getDireccion().getCiudad(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getPrecio(),
                reserva.getEstado()
            ))
            .collect(Collectors.toList());

        return new PageImpl<>(items, pageable, reservasPage.getTotalElements());
    }

    @Override
    public ItemReservaDTO obtenerReservaPorUsuarioYAlojamiento(String usuarioId, Long alojamientoId) throws Exception {
        Optional<Reserva> reservaOpt = reservaRepositorio.findByHuespedIdAndAlojamientoId(usuarioId, alojamientoId);

        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            return new ItemReservaDTO(
                reserva.getId(),
                reserva.getAlojamiento().getId(),
                reserva.getAlojamiento().getTitulo(),
                reserva.getAlojamiento().getDireccion().getCiudad(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getPrecio(),
                reserva.getEstado()
            );
        }

        return null; // No reservation found
    }

    @Override
    public void cancelarReserva(Long id) throws Exception {
        Reserva reserva = reservaRepositorio.findById(id)
            .orElseThrow(() -> new Exception("Reserva no encontrada"));

        reserva.setEstado(ReservaEstado.CANCELADA);
        reservaRepositorio.save(reserva);
    }
}