package com.unicartagena.muebles_ana_api.application.service.impl;

import com.unicartagena.muebles_ana_api.application.entities.DetalleOrdenVenta;
import com.unicartagena.muebles_ana_api.application.entities.DetallePedidoProveedor;
import com.unicartagena.muebles_ana_api.application.entities.MovimientoInventario;
import com.unicartagena.muebles_ana_api.application.entities.OrdenVenta;
import com.unicartagena.muebles_ana_api.application.entities.PedidoProveedor;
import com.unicartagena.muebles_ana_api.application.entities.Usuario;
import com.unicartagena.muebles_ana_api.application.repositories.MovimientoInventarioRepository;
import com.unicartagena.muebles_ana_api.application.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    @Override
    @Transactional
    public void registrarEntradaCompra(PedidoProveedor pedido, DetallePedidoProveedor detalle, Usuario usuario) {
        MovimientoInventario movimiento = MovimientoInventario.entradaCompra(detalle.getProducto(), usuario, pedido, detalle.getCantidad());
        movimientoInventarioRepository.save(movimiento);
    }

    @Override
    @Transactional
    public void registrarSalidaVenta(OrdenVenta orden, DetalleOrdenVenta detalle, Usuario usuario) {
        MovimientoInventario movimiento = MovimientoInventario.salidaVenta(detalle.getProducto(), usuario, orden.getId(), -Math.abs(detalle.getCantidad()));
        movimientoInventarioRepository.save(movimiento);
    }

    @Override
    @Transactional
    public void registrarAnulacionVenta(OrdenVenta orden, DetalleOrdenVenta detalle, Usuario usuario) {
        MovimientoInventario movimiento = MovimientoInventario.anulacionVenta(detalle.getProducto(), usuario, orden.getId(), Math.abs(detalle.getCantidad()));
        movimientoInventarioRepository.save(movimiento);
    }
}
