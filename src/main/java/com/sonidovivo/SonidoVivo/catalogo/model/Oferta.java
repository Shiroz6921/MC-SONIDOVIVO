package com.sonidovivo.SonidoVivo.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Oferta vigente sobre un producto. Cada producto tiene a lo mas una oferta.
 * El precio normal queda en productos.precio; precioAnterior se guarda para
 * mostrar el precio tachado en la web.
 */
@Entity
@Table(name = "ofertas")
@Getter
@Setter
@NoArgsConstructor
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta")
    private Long idOferta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "precio_oferta", nullable = false, precision = 10, scale = 0)
    private BigDecimal precioOferta;

    @Column(name = "precio_anterior", nullable = false, precision = 10, scale = 0)
    private BigDecimal precioAnterior;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;
}
