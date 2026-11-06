package com.unicartagena.muebles_ana_api.application.dto;

import com.unicartagena.muebles_ana_api.application.entities.Rol;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioDTO {

    private Integer id;
    private String nombre;
    private String correo;
    private Rol rol;
    private String documento;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;

}
