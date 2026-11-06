package com.unicartagena.muebles_ana_api.application.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "id_orden_venta")
    private Integer idOrdenVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido_proveedor")
    private PedidoProveedor pedidoProveedor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(length = 255)
    private String observacion;

    private MovimientoInventario(Producto producto, Usuario usuario, PedidoProveedor pedidoProveedor, Integer idOrdenVenta,
                                TipoMovimiento tipoMovimiento, Integer cantidad, String observacion) {
        this.producto = producto;
        this.usuario = usuario;
        this.pedidoProveedor = pedidoProveedor;
        this.idOrdenVenta = idOrdenVenta;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.observacion = observacion;
        this.fechaMovimiento = LocalDateTime.now();
    }

    public static MovimientoInventario entradaCompra(Producto producto, Usuario usuario, PedidoProveedor pedidoProveedor, Integer cantidad) {
        return new MovimientoInventario(producto, usuario, pedidoProveedor, null, TipoMovimiento.ENTRADA_COMPRA, cantidad, null);
    }

    public static MovimientoInventario salidaVenta(Producto producto, Usuario usuario, Integer idOrdenVenta, Integer cantidad) {
        return new MovimientoInventario(producto, usuario, null, idOrdenVenta, TipoMovimiento.SALIDA_VENTA, cantidad, null);
    }

    public static MovimientoInventario anulacionVenta(Producto producto, Usuario usuario, Integer idOrdenVenta, Integer cantidad) {
        return new MovimientoInventario(producto, usuario, null, idOrdenVenta, TipoMovimiento.ANULACION_VENTA, cantidad, null);
    }

    public static MovimientoInventario ajuste(Producto producto, Usuario usuario, Integer cantidad, String observacion) {
        return new MovimientoInventario(producto, usuario, null, null, TipoMovimiento.AJUSTE, cantidad, observacion);
    }
}
