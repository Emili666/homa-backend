package poo.uniquindio.edu.co.Homa.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AlojamientoDao {

    private final AlojamientoRepository alojamientoRepository;

    public Alojamiento guardar(Alojamiento alojamiento) {
        return alojamientoRepository.save(alojamiento);
    }

    public Optional<Alojamiento> buscarPorId(Long id) {
        return alojamientoRepository.findById(id);
    }

    public List<Alojamiento> listarTodos() {
        return alojamientoRepository.findAll();
    }

    public void eliminar(Long id) {
        alojamientoRepository.deleteById(id);
    }
}