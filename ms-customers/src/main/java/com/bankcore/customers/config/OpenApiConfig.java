package com.bankcore.customers.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        String description =
                String.join(
                        " ",
                        "REST API for customer and account management");

        return new OpenAPI()
                .info(new Info().title("BankCoreSystem-Customers").version("1.0").description(description));
    }
}
