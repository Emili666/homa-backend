package poo.uniquindio.edu.co.Homa.dto;

public record ReservaDto(
        Long id,
        Long idUsuario,
        Long idAlojamiento,
        String fechaInicio,
        String fechaFin,
        Double total
) {}