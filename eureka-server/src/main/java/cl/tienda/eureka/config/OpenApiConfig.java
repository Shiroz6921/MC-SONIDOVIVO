package cl.tienda.eureka.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI documentacion() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eureka Server - SonidoVivo")
                        .version("1.0")
                        .description("Servidor de descubrimiento de microservicios."))
                .servers(List.of(new Server().url("http://localhost:8761").description("Eureka")));
    }
}
