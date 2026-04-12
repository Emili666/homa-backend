package poo.uniquindio.edu.co.Homa.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class MercadoPagoService {

    @Value("${mercadopago.access_token}")
    private String accessToken;

    @Value("${mercadopago.back_url_success}")
    private String backUrlSuccess;

    @Value("${mercadopago.back_url_pending}")
    private String backUrlPending;

    @Value("${mercadopago.back_url_failure}")
    private String backUrlFailure;

    /**
     * Crea una preferencia de pago en Mercado Pago.
     *
     * @param title    Título del ítem
     * @param price    Precio unitario
     * @param quantity Cantidad
     * @return ID de la preferencia creada
     */
    public String createPreference(String title, BigDecimal price, Integer quantity)
            throws MPException, MPApiException {

        // Configurar el access token en cada llamada para garantizar el token correcto
        MercadoPagoConfig.setAccessToken(accessToken);

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(title)
                .quantity(quantity)
                .unitPrice(price)
                .currencyId("COP")
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrlSuccess)
                .pending(backUrlPending)
                .failure(backUrlFailure)
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(request);

        log.info("Preferencia de pago creada con ID: {}", preference.getId());
        return preference.getId();
    }
}
