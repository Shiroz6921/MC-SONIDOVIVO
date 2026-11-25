package cl.tienda.pedidos.config;

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
                        .title("API Pedidos - SonidoVivo")
                        .version("1.0")
                        .description("Compras: obtiene precios reales desde productos, descuenta stock, cambia estados y permite cancelar."))
                .servers(List.of(new Server().url("http://localhost:8084").description("Microservicio")));
    }
}
