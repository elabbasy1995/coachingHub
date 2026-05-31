package com.elabbasy.coatchinghub.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class StripeProperties {

    @Value("${app.stripe.secret-key:}")
    private String secretKey;

    @Value("${app.stripe.webhook-secret:}")
    private String webhookSecret;

    @Value("${app.stripe.currency:usd}")
    private String currency;
}
