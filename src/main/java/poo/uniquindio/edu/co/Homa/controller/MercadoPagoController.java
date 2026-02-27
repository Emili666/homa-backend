package poo.uniquindio.edu.co.Homa.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest;
import poo.uniquindio.edu.co.Homa.service.MercadoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for Mercado Pago payments")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping("/create-preference")
    @Operation(summary = "Create a Mercado Pago preference ID")
    public ResponseEntity<?> createPreference(@Valid @RequestBody PaymentRequest request) {
        try {
            String preferenceId = mercadoPagoService.createPreference(
                    request.getTitle(),
                    request.getPrice(),
                    request.getQuantity());
            return ResponseEntity.ok(Map.of("id", preferenceId));
        } catch (MPException | MPApiException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
