package com.unicartagena.muebles_ana_api.application.request;

import com.unicartagena.muebles_ana_api.application.entities.Rol;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioRolRequest {

    @NotNull
    private Rol rol;

}
