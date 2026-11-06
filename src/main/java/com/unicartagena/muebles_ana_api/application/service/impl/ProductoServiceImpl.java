package com.unicartagena.muebles_ana_api.application.service.impl;

import com.unicartagena.muebles_ana_api.application.dto.ProductoDTO;
import com.unicartagena.muebles_ana_api.application.entities.Producto;
import com.unicartagena.muebles_ana_api.application.repositories.ProductoRepository;
import com.unicartagena.muebles_ana_api.application.request.ProductoRequest;
import com.unicartagena.muebles_ana_api.application.service.ProductoService;
import com.unicartagena.muebles_ana_api.base.exceptions.ConflictoException;
import com.unicartagena.muebles_ana_api.base.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ProductoDTO crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new ConflictoException("El código '" + request.getCodigo() + "' ya existe");
        }
        Producto producto = new Producto(request.getCodigo(), request.getNombre(), request.getPrecioVenta());
        producto.setDescripcion(request.getDescripcion());
        producto.setActivo(true);
        productoRepository.save(producto);
        return toDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Integer id) {
        return toDTO(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodos() {
        return productoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerActivos() {
        return productoRepository.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public ProductoDTO actualizar(Integer id, ProductoRequest request) {
        Producto producto = buscarPorId(id);

        if (productoRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new ConflictoException("El código '" + request.getCodigo() + "' ya existe");
        }

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioVenta(request.getPrecioVenta());
        productoRepository.save(producto);
        return toDTO(producto);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
    }

    public ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setActivo(producto.getActivo());
        return dto;
    }
}
