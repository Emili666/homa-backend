package poo.uniquindio.edu.co.homa.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poo.uniquindio.edu.co.homa.dto.request.ActualizarUsuarioRequest;
import poo.uniquindio.edu.co.homa.dto.request.CambiarContrasenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.RecuperarContrasenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.RestablecerContrasenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.homa.dto.response.UsuarioResponse;

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
