package com.unicartagena.muebles_ana_api.application.service;

import com.unicartagena.muebles_ana_api.application.dto.OrdenVentaDTO;
import com.unicartagena.muebles_ana_api.application.request.OrdenVentaRequest;

import java.util.List;

public interface OrdenVentaService {

    OrdenVentaDTO crear(OrdenVentaRequest request, String correoAutenticado);
    OrdenVentaDTO obtenerPorId(Integer id, String correoAutenticado);
    List<OrdenVentaDTO> obtenerTodos(String correoAutenticado);
    OrdenVentaDTO cancelar(Integer id, String correoAutenticado);
    OrdenVentaDTO entregar(Integer id);

}
