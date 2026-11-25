package cl.tienda.carrito.client;

import cl.tienda.carrito.dto.ProductoInfo;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductoClient {

    private final RestClient restClient;

    public ProductoClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://productos").build();
    }

    public ProductoInfo obtener(Long productoId) {
        try {
            return restClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar el microservicio de productos");
        }
    }
}
