package com.unicartagena.muebles_ana_api.application.dto;

import lombok.Data;

@Data
public class ProveedorDTO {

    private Integer id;
    private String nombre;
    private String documento;
    private String contacto;
    private String telefono;
    private String correo;
    private Boolean activo;

}
