package com.unicartagena.muebles_ana_api.application.service;

import com.unicartagena.muebles_ana_api.application.dto.ProductoDTO;
import com.unicartagena.muebles_ana_api.application.request.ProductoRequest;

import java.util.List;

public interface ProductoService {

    ProductoDTO crear(ProductoRequest request);
    ProductoDTO obtenerPorId(Integer id);
    List<ProductoDTO> obtenerTodos();
    List<ProductoDTO> obtenerActivos();
    ProductoDTO actualizar(Integer id, ProductoRequest request);
    void eliminar(Integer id);
}
