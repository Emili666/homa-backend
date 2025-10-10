package main.java.poo.uniquindio.edu.co.Homa.dto;
public record LocalizacionDto(       @NotNull @Min(-90) @Max(90)
        Double latitud,
        @NotNull @Min(-180) @Max(180)
        Double longitud) {
    
}
