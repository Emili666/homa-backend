package poo.uniquindio.edu.co.homa.service;

import poo.uniquindio.edu.co.homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.homa.dto.response.LoginResponse;

public interface AuthService {
    
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    LoginResponse refreshToken(String refreshToken);
}
