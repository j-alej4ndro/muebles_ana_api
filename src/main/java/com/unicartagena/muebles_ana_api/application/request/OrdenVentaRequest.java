package com.unicartagena.muebles_ana_api.application.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrdenVentaRequest {

    @NotEmpty
    @Valid
    private List<DetalleOrdenVentaItemRequest> detalles;

}
