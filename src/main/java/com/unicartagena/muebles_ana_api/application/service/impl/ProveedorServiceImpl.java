package com.unicartagena.muebles_ana_api.application.service.impl;

import com.unicartagena.muebles_ana_api.application.dto.ProveedorDTO;
import com.unicartagena.muebles_ana_api.application.entities.Proveedor;
import com.unicartagena.muebles_ana_api.application.repositories.ProveedorRepository;
import com.unicartagena.muebles_ana_api.application.request.ProveedorRequest;
import com.unicartagena.muebles_ana_api.application.request.ProveedorUpdateRequest;
import com.unicartagena.muebles_ana_api.application.service.ProveedorService;
import com.unicartagena.muebles_ana_api.base.exceptions.ConflictoException;
import com.unicartagena.muebles_ana_api.base.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public ProveedorDTO crear(ProveedorRequest request) {
        if (proveedorRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoException("El documento '" + request.getDocumento() + "' ya está registrado");
        }
        Proveedor proveedor = new Proveedor(request.getNombre(), request.getDocumento());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setActivo(true);
        proveedorRepository.save(proveedor);
        return toDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorId(Integer id) {
        return toDTO(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> obtenerTodos() {
        return proveedorRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public ProveedorDTO actualizar(Integer id, ProveedorUpdateRequest request) {
        Proveedor proveedor = buscarPorId(id);

        if (proveedorRepository.existsByDocumentoAndIdNot(request.getDocumento(), id)) {
            throw new ConflictoException("El documento '" + request.getDocumento() + "' ya está registrado");
        }

        proveedor.setNombre(request.getNombre());
        proveedor.setDocumento(request.getDocumento());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCorreo(request.getCorreo());
        if (request.getActivo() != null) {
            proveedor.setActivo(request.getActivo());
        }
        proveedorRepository.save(proveedor);
        return toDTO(proveedor);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    private Proveedor buscarPorId(Integer id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id " + id));
    }

    public ProveedorDTO toDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setDocumento(proveedor.getDocumento());
        dto.setContacto(proveedor.getContacto());
        dto.setTelefono(proveedor.getTelefono());
        dto.setCorreo(proveedor.getCorreo());
        dto.setActivo(proveedor.getActivo());
        return dto;
    }
}
