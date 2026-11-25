package cl.duoc.mensajeria.config;

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
                        .title("API Mensajería - SonidoVivo")
                        .version("1.0")
                        .description("Mensajes del formulario de contacto y su estado (PENDIENTE / ATENDIDO)."))
                .servers(List.of(new Server().url("http://localhost:8082").description("Microservicio")));
    }
}
