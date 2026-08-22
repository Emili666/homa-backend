package poo.uniquindio.edu.co.Homa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {
    private String mensaje;
    private Object data;
    
    public MessageResponseDTO(String mensaje) {
        this.mensaje = mensaje;
    }
}
