package com.unicartagena.muebles_ana_api.application.request;

import com.unicartagena.muebles_ana_api.application.entities.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUsuarioCreateRequest {

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotNull
    private Rol rol;

    @Size(max = 30)
    private String documento;

    @Size(max = 30)
    private String telefono;

    @Size(max = 150)
    private String direccion;

}
