package poo.uniquindio.edu.co.Homa.dto;

public record ReservaDto(
        String id,
        String idUsuario,
        String idAlojamiento,
        String fechaInicio,
        String fechaFin,
        Double total
) {}