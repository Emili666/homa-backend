package poo.uniquindio.edu.co.Homa.dto;

public record AuthResponseDto(
        String token,
        String tipo
) {
    public AuthResponseDto(String token) {
        this(token, "Bearer");
    }
}
