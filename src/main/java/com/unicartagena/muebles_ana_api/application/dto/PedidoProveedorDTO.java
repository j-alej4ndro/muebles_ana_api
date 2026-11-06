package com.unicartagena.muebles_ana_api.application.dto;

import com.unicartagena.muebles_ana_api.application.entities.EstadoPedido;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoProveedorDTO {

    private Integer id;
    private Integer idProveedor;
    private String nombreProveedor;
    private Integer idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private List<DetallePedidoProveedorDTO> detalles;

}
