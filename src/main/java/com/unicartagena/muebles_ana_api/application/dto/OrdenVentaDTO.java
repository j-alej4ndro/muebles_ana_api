package com.unicartagena.muebles_ana_api.application.dto;

import com.unicartagena.muebles_ana_api.application.entities.EstadoOrden;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenVentaDTO {

    private Integer id;
    private Integer idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private LocalDateTime fechaOrden;
    private EstadoOrden estado;
    private List<DetalleOrdenVentaDTO> detalles;
    private BigDecimal total;

}
