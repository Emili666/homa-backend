package main.java.poo.uniquindio.edu.co.Homa.dto;


public record CreacionAlojamientoDto(

        @NotBlank @Length(max = 150) 
        String titulo,

        @NotBlank
        String descripcion,

        @NotNull 
        int maxHuespedes,

        @NotNull
        DireccionDTO direccion,

        @NotNull @Min(0)
        Float precioPorNoche,

        List<Servicio> servicios,

        @Size(min = 1, max = 10)
        List<String> imagenes

) {
}