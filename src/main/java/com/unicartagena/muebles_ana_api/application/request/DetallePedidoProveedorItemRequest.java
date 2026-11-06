package com.unicartagena.muebles_ana_api.application.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetallePedidoProveedorItemRequest {

    @NotNull
    private Integer idProducto;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal precioUnitario;

}
