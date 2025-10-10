package poo.uniquindio.edu.co.Homa.dto;

public record DireccionDto(
        @NotBlank
        String ciudad,
        @NotBlank
        String direccion,
        @NotNull
        LocalizacionDTO localizacion
) {
}
