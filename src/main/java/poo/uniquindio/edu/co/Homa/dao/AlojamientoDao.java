package poo.uniquindio.edu.co.Homa.dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

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

    // 🔍 Consultas personalizadas
    public List<Alojamiento> buscarPorCiudad(String ciudad) {
        return entityManager.createQuery(
                        "SELECT a FROM Alojamiento a WHERE LOWER(a.direccion) LIKE LOWER(:ciudad)", Alojamiento.class)
                .setParameter("ciudad", "%" + ciudad + "%")
                .getResultList();
    }

    public List<Alojamiento> buscarPorPrecio(Double min, Double max) {
        return entityManager.createQuery(
                        "SELECT a FROM Alojamiento a WHERE a.precioPorNoche BETWEEN :min AND :max", Alojamiento.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }
}