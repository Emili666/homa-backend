package co.edu.uniquindio.homa.service;

import co.edu.uniquindio.homa.dto.request.LoginRequest;
import co.edu.uniquindio.homa.dto.response.LoginResponse;

public interface AuthService {
    
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    LoginResponse refreshToken(String refreshToken);
}
