package poo.uniquindio.edu.co.Homa.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access_token:PLACEHOLDER_TOKEN}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (!"PLACEHOLDER_TOKEN".equals(accessToken)) {
            MercadoPagoConfig.setAccessToken(accessToken);
        }
    }
}
