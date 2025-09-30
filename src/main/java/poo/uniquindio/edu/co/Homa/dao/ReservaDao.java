
package poo.uniquindio.edu.co.Homa.dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import poo.uniquindio.edu.co.Homa.model.Reserva;
import poo.uniquindio.edu.co.Homa.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservaDao {

    private final ReservaRepository reservaRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    // 🔍 Consultas personalizadas con JPQL
    public List<Reserva> buscarPorFechas(LocalDate inicio, LocalDate fin) {
        return entityManager.createQuery(
                        "SELECT r FROM Reserva r WHERE r.fechaInicio >= :inicio AND r.fechaFin <= :fin", Reserva.class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public List<Reserva> buscarPorUsuario(Long usuarioId) {
        return entityManager.createQuery(
                        "SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId", Reserva.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }
}
