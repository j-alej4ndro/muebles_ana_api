package com.unicartagena.muebles_ana_api.application.service;

import com.unicartagena.muebles_ana_api.application.dto.ProveedorDTO;
import com.unicartagena.muebles_ana_api.application.request.ProveedorRequest;
import com.unicartagena.muebles_ana_api.application.request.ProveedorUpdateRequest;

import java.util.List;

public interface ProveedorService {

    ProveedorDTO crear(ProveedorRequest request);
    ProveedorDTO obtenerPorId(Integer id);
    List<ProveedorDTO> obtenerTodos();
    ProveedorDTO actualizar(Integer id, ProveedorUpdateRequest request);
    void eliminar(Integer id);

}
