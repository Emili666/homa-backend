package poo.uniquindio.edu.co.Homa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;
import poo.uniquindio.edu.co.Homa.mapper.AlojamientoMapper;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.service.AlojamientoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlojamientoServiceImpl implements AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;

    @Override
    public List<AlojamientoDto> listarAlojamientos() {
        return alojamientoRepository.findAll()
                .stream()
                .map(AlojamientoMapper::toDto)
                .toList();
    }

    @Override
    public AlojamientoDto obtenerAlojamientoPorId(Long id) {
        return alojamientoRepository.findById(id)
                .map(AlojamientoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado con id: " + id));
    }

    @Override
    public AlojamientoDto crearAlojamiento(AlojamientoDto dto) {
        Alojamiento alojamiento = AlojamientoMapper.toEntity(dto);
        Alojamiento guardado = alojamientoRepository.save(alojamiento);
        return AlojamientoMapper.toDto(guardado);
    }

    @Override
    public AlojamientoDto actualizarAlojamiento(Long id, AlojamientoDto dto) {
        Alojamiento existente = alojamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado con id: " + id));

        existente.setTitulo(dto.titulo());
        existente.setDescripcion(dto.descripcion());
        existente.setDireccion(dto.direccion());
        existente.setPrecioPorNoche(dto.precioPorNoche().doubleValue());
        existente.setMaxHuespedes(dto.maxHuespedes());
        existente.setServicios(dto.servicios());
        existente.setImagenes(dto.imagenes());
        existente.setNombreAnfitrion(dto.nombreAnfitrion());

        return AlojamientoMapper.toDto(alojamientoRepository.save(existente));
    }

    @Override
    public void eliminarAlojamiento(Long id) {
        if (!alojamientoRepository.existsById(id)) {
            throw new RuntimeException("No existe un alojamiento con id: " + id);
        }
        alojamientoRepository.deleteById(id);
    }
}