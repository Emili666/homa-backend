package poo.uniquindio.edu.co.Homa.service;

import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;

import java.util.List;

public interface AlojamientoService {

    List<AlojamientoDto> listarAlojamientos();

    AlojamientoDto obtenerAlojamientoPorId(Long id);

    AlojamientoDto crearAlojamiento(AlojamientoDto dto);

    AlojamientoDto actualizarAlojamiento(Long id, AlojamientoDto dto);

    void eliminarAlojamiento(Long id);
}
