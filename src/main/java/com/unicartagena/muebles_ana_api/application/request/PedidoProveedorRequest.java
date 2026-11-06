package com.unicartagena.muebles_ana_api.application.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoProveedorRequest {

    @NotNull
    private Integer idProveedor;

    @NotEmpty
    @Valid
    private List<DetallePedidoProveedorItemRequest> detalles;

}
