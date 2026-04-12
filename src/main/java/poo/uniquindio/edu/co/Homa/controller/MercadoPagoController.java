package poo.uniquindio.edu.co.Homa.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest;
import poo.uniquindio.edu.co.Homa.service.MercadoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Endpoints para pagos con Mercado Pago")
@SecurityRequirement(name = "bearerAuth")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping("/create-preference")
    @Operation(summary = "Crear preferencia de pago en Mercado Pago")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<?> createPreference(@Valid @RequestBody PaymentRequest request) {
        try {
            String preferenceId = mercadoPagoService.createPreference(
                    request.getTitle(),
                    request.getPrice(),
                    request.getQuantity());
            return ResponseEntity.ok(Map.of("id", preferenceId));
        } catch (MPApiException e) {
            String mpError = e.getApiResponse() != null ? e.getApiResponse().getContent() : "sin respuesta";
            log.error("Error de API Mercado Pago (status {}): {}", e.getStatusCode(), mpError);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al procesar el pago.",
                "detalle", mpError
            ));
        } catch (MPException e) {
            log.error("Error de Mercado Pago: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al procesar el pago."));
        }
    }
}
