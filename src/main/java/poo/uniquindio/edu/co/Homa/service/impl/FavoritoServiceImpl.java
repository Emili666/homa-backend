package poo.uniquindio.edu.co.Homa.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.dto.response.AlojamientoResponse;
import poo.uniquindio.edu.co.Homa.exception.BusinessException;
import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.mapper.AlojamientoMapper;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Favorito;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.repository.FavoritoRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.service.FavoritoService;
import poo.uniquindio.edu.co.Homa.exception.UnauthorizedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoMapper alojamientoMapper;

    @Override
    @Transactional
    public void marcarFavorito(Long usuarioId, Long alojamientoId) {
        log.info("Marcando alojamiento {} como favorito para usuario {}", alojamientoId, usuarioId);

        if (favoritoRepository.existsByUsuarioIdAndAlojamientoId(usuarioId, alojamientoId)) {
            throw new BusinessException("El alojamiento ya está marcado como favorito");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + alojamientoId));

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .alojamiento(alojamiento)
                .build();

        favoritoRepository.save(favorito);
        log.info("Favorito creado exitosamente para usuario {} y alojamiento {}", usuarioId, alojamientoId);
    }

    @Override
    @Transactional
    public void quitarFavorito(Long usuarioId, Long alojamientoId) {
        log.info("Quitando alojamiento {} de favoritos del usuario {}", alojamientoId, usuarioId);

        Favorito favorito = favoritoRepository.findByUsuarioIdAndAlojamientoId(usuarioId, alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("El alojamiento no está marcado como favorito"));

        favoritoRepository.delete(favorito);
        log.info("Favorito eliminado para usuario {} y alojamiento {}", usuarioId, alojamientoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlojamientoResponse> listarFavoritos(Long usuarioId, Pageable pageable) {
        log.info("Listando favoritos del usuario {}", usuarioId);

        return favoritoRepository.findByUsuarioId(usuarioId, pageable)
                .map(favorito -> {
                    Alojamiento alojamiento = favorito.getAlojamiento();
                    AlojamientoResponse response = alojamientoMapper.toResponse(alojamiento);
                    response.setTotalFavoritos(favoritoRepository.countByAlojamientoId(alojamiento.getId()));
                    response.setEsFavorito(Boolean.TRUE);
                    return response;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public long contarFavoritos(Long alojamientoId) {
        return favoritoRepository.countByAlojamientoId(alojamientoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esFavorito(Long usuarioId, Long alojamientoId) {
        return favoritoRepository.existsByUsuarioIdAndAlojamientoId(usuarioId, alojamientoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarFavoritosParaAnfitrion(Long alojamientoId, Long anfitrionId) {
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + alojamientoId));

        if (!alojamiento.getAnfitrion().getId().equals(anfitrionId)) {
            throw new UnauthorizedException("No tienes permiso para ver los favoritos de este alojamiento");
        }

        return favoritoRepository.countByAlojamientoId(alojamientoId);
    }
}
