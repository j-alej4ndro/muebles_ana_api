package com.unicartagena.muebles_ana_api.application.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String correo;

    @NotBlank
    private String password;
}
