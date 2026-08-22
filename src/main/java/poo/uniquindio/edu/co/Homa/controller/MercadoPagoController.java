package poo.uniquindio.edu.co.Homa.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.config.MetricsConfig.HomaBusinessMetrics;
import poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest;
import poo.uniquindio.edu.co.Homa.service.MercadoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Endpoints para pagos con Mercado Pago")
@SecurityRequirement(name = "bearerAuth")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;
    private final HomaBusinessMetrics metrics;

    @PostMapping("/create-preference")
    @Operation(summary = "Crear preferencia de pago en Mercado Pago")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPreference(@Valid @RequestBody PaymentRequest request) {
        long inicio = System.currentTimeMillis();
        try {
            String preferenceId = mercadoPagoService.createPreference(
                    request.getTitle(),
                    request.getPrice(),
                    request.getQuantity());

            long duracion = System.currentTimeMillis() - inicio;
            metrics.getTimerPago().record(duracion, TimeUnit.MILLISECONDS);
            metrics.incrementPagoExitoso();

            log.info("Preferencia de pago creada exitosamente en {}ms", duracion);
            return ResponseEntity.ok(Map.of("id", preferenceId));

        } catch (MPApiException e) {
            long duracion = System.currentTimeMillis() - inicio;
            metrics.getTimerPago().record(duracion, TimeUnit.MILLISECONDS);
            metrics.incrementPagoFallido();

            String mpError = e.getApiResponse() != null ? e.getApiResponse().getContent() : "sin respuesta";
            log.error("Error de API Mercado Pago (status {}) en {}ms: {}", e.getStatusCode(), duracion, mpError);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al procesar el pago.",
                "detalle", mpError
            ));
        } catch (MPException e) {
            long duracion = System.currentTimeMillis() - inicio;
            metrics.getTimerPago().record(duracion, TimeUnit.MILLISECONDS);
            metrics.incrementPagoFallido();

            log.error("Error de Mercado Pago en {}ms: {}", duracion, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al procesar el pago."));
        }
    }

    @PostMapping("/reintento")
    @Operation(summary = "Registrar reintento de pago")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registrarReintento() {
        metrics.incrementPagoReintento();
        log.info("Reintento de pago registrado");
        return ResponseEntity.ok(Map.of("mensaje", "Reintento registrado"));
    }
}
