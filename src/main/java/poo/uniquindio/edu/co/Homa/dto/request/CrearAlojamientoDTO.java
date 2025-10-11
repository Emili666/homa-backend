package poo.uniquindio.edu.co.homa.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearAlojamientoDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String titulo;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 255, message = "La ciudad no puede exceder 255 caracteres")
    private String ciudad;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    private Float latitud;

    private Float longitud;

    @NotNull(message = "El precio por noche es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Float precioPorNoche;

    @NotNull(message = "El máximo de huéspedes es obligatorio")
    @Positive(message = "El máximo de huéspedes debe ser positivo")
    private Integer maxHuespedes;

    @NotEmpty(message = "Debe proporcionar al menos una imagen")
    @Size(min = 1, max = 10, message = "Debe proporcionar entre 1 y 10 imágenes")
    private List<String> imagenes;

    private List<Integer> servicios;
}
