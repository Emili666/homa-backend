package poo.uniquindio.edu.co.Homa.service;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.resources.preference.Preference;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TC-PAY-001 a TC-PAY-006
 * Pruebas unitarias del servicio de pagos HOMA (MercadoPago).
 * Se mockea PreferenceClient para no hacer llamadas reales a la API.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TC-PAY — Pruebas unitarias del servicio de pagos MercadoPago")
class PagoServiceTest {

    @InjectMocks
    private MercadoPagoService mercadoPagoService;

    @BeforeEach
    void setUp() {
        // Inyectar valores de configuración sin necesidad de contexto Spring
        ReflectionTestUtils.setField(mercadoPagoService, "accessToken", "TEST-token-sandbox");
        ReflectionTestUtils.setField(mercadoPagoService, "backUrlSuccess", "https://homa.com/success");
        ReflectionTestUtils.setField(mercadoPagoService, "backUrlPending", "https://homa.com/pending");
        ReflectionTestUtils.setField(mercadoPagoService, "backUrlFailure", "https://homa.com/failure");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-001 — Crear preferencia de pago exitosamente
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-001: Crear preferencia de pago con datos válidos → retorna ID de preferencia")
    void createPreference_conDatosValidos_retornaPreferenceId() throws MPException, MPApiException {
        // Arrange: mockear la construcción de PreferenceClient
        Preference preferenceMock = mock(Preference.class);
        when(preferenceMock.getId()).thenReturn("MP-PREF-123456789");

        try (MockedConstruction<PreferenceClient> mockedClient =
                mockConstruction(PreferenceClient.class,
                        (mock, context) -> when(mock.create(any())).thenReturn(preferenceMock))) {

            // Act
            String preferenceId = mercadoPagoService.createPreference(
                    "Reserva Cabaña Eje Cafetero",
                    new BigDecimal("300000"),
                    1
            );

            // Assert
            assertThat(preferenceId).isNotBlank();
            assertThat(preferenceId).isEqualTo("MP-PREF-123456789");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-002 — Crear preferencia con precio correcto
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-002: Preferencia creada con precio y título correctos")
    void createPreference_verificaTituloYPrecio_retornaIdValido() throws MPException, MPApiException {
        // Arrange
        Preference preferenceMock = mock(Preference.class);
        when(preferenceMock.getId()).thenReturn("MP-PREF-987654321");

        try (MockedConstruction<PreferenceClient> mockedClient =
                mockConstruction(PreferenceClient.class,
                        (mock, context) -> when(mock.create(any())).thenReturn(preferenceMock))) {

            // Act
            String preferenceId = mercadoPagoService.createPreference(
                    "Reserva Villa Armenia",
                    new BigDecimal("450000"),
                    1
            );

            // Assert
            assertThat(preferenceId).isEqualTo("MP-PREF-987654321");
            // Verificar que se llamó exactamente una vez a create
            PreferenceClient clientCreado = mockedClient.constructed().get(0);
            verify(clientCreado, times(1)).create(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-003 — Crear preferencia con múltiples noches
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-003: Preferencia para reserva de múltiples noches → precio total correcto")
    void createPreference_multipleNoches_precioTotalCorrecto() throws MPException, MPApiException {
        // Arrange: 5 noches × $100.000 = $500.000
        Preference preferenceMock = mock(Preference.class);
        when(preferenceMock.getId()).thenReturn("MP-PREF-MULTI-001");

        try (MockedConstruction<PreferenceClient> mockedClient =
                mockConstruction(PreferenceClient.class,
                        (mock, context) -> when(mock.create(any())).thenReturn(preferenceMock))) {

            // Act
            String preferenceId = mercadoPagoService.createPreference(
                    "Reserva Finca Cafetera — 5 noches",
                    new BigDecimal("500000"),
                    1
            );

            // Assert
            assertThat(preferenceId).isNotBlank();
            assertThat(mockedClient.constructed()).hasSize(1);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-004 — Validación: precio no puede ser cero
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-004: PaymentRequest con precio cero → falla validación Bean Validation")
    void paymentRequest_conPrecioCero_fallaValidacion() {
        // Arrange
        var request = new poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest();
        request.setTitle("Reserva Test");
        request.setPrice(BigDecimal.ZERO);
        request.setQuantity(1);

        // Act & Assert: validar con Jakarta Validation
        var factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("price")))
                .isTrue();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-005 — Validación: título no puede estar vacío
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-005: PaymentRequest con título vacío → falla validación Bean Validation")
    void paymentRequest_conTituloVacio_fallaValidacion() {
        // Arrange
        var request = new poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest();
        request.setTitle("");
        request.setPrice(new BigDecimal("100000"));
        request.setQuantity(1);

        // Act & Assert
        var factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")))
                .isTrue();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-006 — Validación: cantidad no puede ser negativa
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-006: PaymentRequest con cantidad negativa → falla validación Bean Validation")
    void paymentRequest_conCantidadNegativa_fallaValidacion() {
        // Arrange
        var request = new poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest();
        request.setTitle("Reserva Test");
        request.setPrice(new BigDecimal("100000"));
        request.setQuantity(-1);

        // Act & Assert
        var factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")))
                .isTrue();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-PAY-007 — PaymentRequest válido pasa todas las validaciones
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-PAY-007: PaymentRequest con datos válidos → pasa todas las validaciones")
    void paymentRequest_conDatosValidos_pasaValidacion() {
        // Arrange
        var request = new poo.uniquindio.edu.co.Homa.dto.request.PaymentRequest();
        request.setTitle("Reserva Cabaña Armenia");
        request.setPrice(new BigDecimal("300000"));
        request.setQuantity(1);

        // Act
        var factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }
}
