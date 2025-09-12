
package poo.uniquindio.edu.co.Homa.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import poo.uniquindio.edu.co.Homa.model.Reserva;
import poo.uniquindio.edu.co.Homa.repository.ReservaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservaDao {

    private final ReservaRepository reservaRepository;

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }
}
