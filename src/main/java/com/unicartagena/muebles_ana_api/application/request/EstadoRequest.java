package com.unicartagena.muebles_ana_api.application.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstadoRequest {

    @NotNull
    private Boolean activo;

}
