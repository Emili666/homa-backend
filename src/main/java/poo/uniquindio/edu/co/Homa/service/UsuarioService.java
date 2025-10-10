package poo.uniquindio.edu.co.homa.service;

import co.edu.uniquindio.homa.dto.request.*;
import co.edu.uniquindio.homa.dto.response.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    
    UsuarioResponse registrar(UsuarioRegistroRequest request);
    
    UsuarioResponse obtenerPorId(Long id);
    
    UsuarioResponse obtenerPorEmail(String email);
    
    UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request);
    
    void eliminar(Long id);
    
    void cambiarContrasena(Long id, CambiarContrasenaRequest request);
    
    void solicitarRecuperacionContrasena(RecuperarContrasenaRequest request);
    
    void restablecerContrasena(RestablecerContrasenaRequest request);
    
    void activarCuenta(String codigo);
    
    Page<UsuarioResponse> listarTodos(Pageable pageable);
    
    void cambiarEstado(Long id, String estado);
}
