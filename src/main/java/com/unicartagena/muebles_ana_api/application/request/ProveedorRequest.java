package com.unicartagena.muebles_ana_api.application.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @NotBlank
    @Size(max = 30)
    private String documento;

    @Size(max = 100)
    private String contacto;

    @Size(max = 30)
    private String telefono;

    @Email
    @Size(max = 100)
    private String correo;

}
