
public record EdicionAlojamientoDTO(

        @NotBlank @Length(max = 150)
        String titulo,

        @NotBlank
        String descripcion,

        @NotNull 
        int maxHuespedes,

        @NotNull @Min(0)
        Float precioPorNoche,

        @NotNull
        List<Servicio> servicios,

        @NotNull @Size(max = 10)
        List<String> imagenes,

        @NotNull
        DireccionDTO direccion
) {
}