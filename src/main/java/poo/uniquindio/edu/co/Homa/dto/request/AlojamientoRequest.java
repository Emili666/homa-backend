package poo.uniquindio.edu.co.homa.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poo.uniquindio.edu.co.homa.model.enums.Servicio;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlojamientoRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 10, max = 150, message = "El título debe tener entre 10 y 150 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 50, max = 2000, message = "La descripción debe tener entre 50 y 2000 caracteres")
    private String descripcion;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Float latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Float longitud;

    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Float precioPorNoche;

    @NotNull(message = "El máximo de huéspedes es obligatorio")
    @Min(value = 1, message = "Debe permitir al menos 1 huésped")
    private Integer maxHuespedes;

    @NotEmpty(message = "Debe incluir al menos una imagen")
    @Size(min = 1, max = 10, message = "Debe incluir entre 1 y 10 imágenes")
    private List<String> imagenes;

    private List<Servicio> servicios;
}
