package com.unicartagena.muebles_ana_api.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetallePedidoProveedorDTO {

    private Integer id;
    private Integer idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

}
