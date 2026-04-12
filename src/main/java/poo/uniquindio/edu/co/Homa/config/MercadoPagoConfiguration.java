package poo.uniquindio.edu.co.Homa.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access_token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("MERCADO_PAGO_ACCESS_TOKEN no configurado. Los pagos no funcionarán.");
            return;
        }
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago configurado correctamente.");
    }
}
