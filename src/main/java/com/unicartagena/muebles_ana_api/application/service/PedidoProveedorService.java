package com.unicartagena.muebles_ana_api.application.service;

import com.unicartagena.muebles_ana_api.application.dto.PedidoProveedorDTO;
import com.unicartagena.muebles_ana_api.application.request.PedidoProveedorRequest;

import java.util.List;

public interface PedidoProveedorService {

    PedidoProveedorDTO crear(PedidoProveedorRequest request, String correoAutenticado);
    PedidoProveedorDTO obtenerPorId(Integer id);
    List<PedidoProveedorDTO> obtenerTodos();
    PedidoProveedorDTO recibir(Integer id, String correoAutenticado);
    PedidoProveedorDTO cancelar(Integer id, String correoAutenticado);

}
