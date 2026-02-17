package poo.uniquindio.edu.co.Homa.service;

import poo.uniquindio.edu.co.Homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.Homa.dto.response.LoginResponse;

public interface AuthService {
    
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    LoginResponse refreshToken(String refreshToken);
}
