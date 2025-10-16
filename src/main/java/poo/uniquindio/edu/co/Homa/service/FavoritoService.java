package poo.uniquindio.edu.co.homa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poo.uniquindio.edu.co.homa.dto.response.AlojamientoResponse;

public interface FavoritoService {

    void marcarFavorito(Long usuarioId, Long alojamientoId);

    void quitarFavorito(Long usuarioId, Long alojamientoId);

    Page<AlojamientoResponse> listarFavoritos(Long usuarioId, Pageable pageable);

    long contarFavoritos(Long alojamientoId);

    boolean esFavorito(Long usuarioId, Long alojamientoId);

    long contarFavoritosParaAnfitrion(Long alojamientoId, Long anfitrionId);
}
