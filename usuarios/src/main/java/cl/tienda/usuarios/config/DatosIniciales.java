package cl.tienda.usuarios.config;

import cl.tienda.usuarios.model.Usuario;
import cl.tienda.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {
    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("SonidoVivo");
            admin.setEmail("admin@sonidovivo.com");
            admin.setPassword(encoder.encode("1234"));
            admin.setRol("ADMINISTRADOR");
            admin.setActivo(true);
            repository.save(admin);

            Usuario vendedor = new Usuario();
            vendedor.setNombre("Vendedor");
            vendedor.setApellido("Demo");
            vendedor.setEmail("vendedor@sonidovivo.com");
            vendedor.setPassword(encoder.encode("1234"));
            vendedor.setRol("VENDEDOR");
            vendedor.setActivo(true);
            repository.save(vendedor);
        };
    }
}
