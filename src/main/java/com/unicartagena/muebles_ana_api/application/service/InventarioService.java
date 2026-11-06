package com.unicartagena.muebles_ana_api.application.service;

import com.unicartagena.muebles_ana_api.application.entities.DetalleOrdenVenta;
import com.unicartagena.muebles_ana_api.application.entities.DetallePedidoProveedor;
import com.unicartagena.muebles_ana_api.application.entities.OrdenVenta;
import com.unicartagena.muebles_ana_api.application.entities.PedidoProveedor;
import com.unicartagena.muebles_ana_api.application.entities.Usuario;

public interface InventarioService {

    void registrarEntradaCompra(PedidoProveedor pedido, DetallePedidoProveedor detalle, Usuario usuario);
    void registrarSalidaVenta(OrdenVenta orden, DetalleOrdenVenta detalle, Usuario usuario);
    void registrarAnulacionVenta(OrdenVenta orden, DetalleOrdenVenta detalle, Usuario usuario);

}
