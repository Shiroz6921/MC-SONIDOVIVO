package com.sonidovivo.SonidoVivo.contacto.service;

import com.sonidovivo.SonidoVivo.contacto.model.Contacto;
import com.sonidovivo.SonidoVivo.contacto.model.ContactoRequest;
import com.sonidovivo.SonidoVivo.contacto.model.ContactoResponse;
import com.sonidovivo.SonidoVivo.contacto.model.EstadoContacto;
import com.sonidovivo.SonidoVivo.contacto.repository.ContactoRepository;
import com.sonidovivo.SonidoVivo.excepciones.RecursoNoEncontradoException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactoService {

    private final ContactoRepository contactos;

    public ContactoService(ContactoRepository contactos) {
        this.contactos = contactos;
    }

    /** Registra el mensaje enviado por el formulario publico de la web. */
    @Transactional
    public ContactoResponse registrar(ContactoRequest peticion) {
        Contacto contacto = new Contacto();
        contacto.setNombre(peticion.nombre().trim());
        contacto.setEmail(peticion.email().trim().toLowerCase());
        contacto.setTelefono((peticion.telefono() == null || peticion.telefono().isBlank())
                ? null
                : peticion.telefono().trim());
        contacto.setAsunto(peticion.asunto().trim());
        contacto.setMensaje(peticion.mensaje().trim());
        contacto.setEstado(EstadoContacto.PENDIENTE);
        contacto.setFechaEnvio(LocalDateTime.now());
        return aRespuesta(contactos.save(contacto));
    }

    /** Bandeja del vendedor/administrador, con filtro por estado. */
    @Transactional(readOnly = true)
    public List<ContactoResponse> listar(EstadoContacto estado) {
        List<Contacto> lista = estado == null
                ? contactos.findAllByOrderByFechaEnvioDesc()
                : contactos.findByEstadoOrderByFechaEnvioDesc(estado);
        return lista.stream().map(ContactoService::aRespuesta).toList();
    }

    /** El vendedor marca el mensaje como atendido (o lo vuelve a pendiente). */
    @Transactional
    public ContactoResponse cambiarEstado(Long id, String nuevoEstado) {
        Contacto contacto = buscar(id);
        contacto.setEstado(EstadoContacto.valueOf(nuevoEstado));
        return aRespuesta(contactos.save(contacto));
    }

    @Transactional
    public void eliminar(Long id) {
        Contacto contacto = buscar(id);
        contactos.delete(contacto);
    }

    private Contacto buscar(Long id) {
        return contactos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje " + id + " no existe"));
    }

    private static ContactoResponse aRespuesta(Contacto contacto) {
        return new ContactoResponse(
                contacto.getIdContacto(),
                contacto.getNombre(),
                contacto.getEmail(),
                contacto.getTelefono(),
                contacto.getAsunto(),
                contacto.getMensaje(),
                contacto.getEstado().name(),
                contacto.getFechaEnvio()
        );
    }
}
