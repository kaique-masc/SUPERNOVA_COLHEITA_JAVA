package br.com.fiap.supernova.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Supernova Colheita API",
        version = "1.0.0",
        description = "Conecta pequenos produtores da agricultura familiar a escolas, restaurantes e feiras, "
                + "aplicando as regras do PNAE. Faca login em /auth/login e use o token no botao Authorize."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
