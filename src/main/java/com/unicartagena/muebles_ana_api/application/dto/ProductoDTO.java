package com.unicartagena.muebles_ana_api.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDTO {

    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private Boolean activo = true;

}
