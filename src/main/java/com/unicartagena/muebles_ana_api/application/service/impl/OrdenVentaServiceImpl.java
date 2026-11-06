package com.unicartagena.muebles_ana_api.application.service.impl;

import com.unicartagena.muebles_ana_api.application.dto.DetalleOrdenVentaDTO;
import com.unicartagena.muebles_ana_api.application.dto.OrdenVentaDTO;
import com.unicartagena.muebles_ana_api.application.entities.*;
import com.unicartagena.muebles_ana_api.application.repositories.MovimientoInventarioRepository;
import com.unicartagena.muebles_ana_api.application.repositories.OrdenVentaRepository;
import com.unicartagena.muebles_ana_api.application.repositories.ProductoRepository;
import com.unicartagena.muebles_ana_api.application.repositories.UsuarioRepository;
import com.unicartagena.muebles_ana_api.application.request.OrdenVentaRequest;
import com.unicartagena.muebles_ana_api.application.service.InventarioService;
import com.unicartagena.muebles_ana_api.application.service.OrdenVentaService;
import com.unicartagena.muebles_ana_api.base.exceptions.ConflictoException;
import com.unicartagena.muebles_ana_api.base.exceptions.RecursoNoEncontradoException;
import com.unicartagena.muebles_ana_api.base.exceptions.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrdenVentaServiceImpl implements OrdenVentaService {

    private final OrdenVentaRepository ordenVentaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final InventarioService inventarioService;

    @Override
    @Transactional
    public OrdenVentaDTO crear(OrdenVentaRequest request, String correoAutenticado) {
        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new SolicitudInvalidaException("La orden debe tener al menos un detalle");
        }

        Set<Integer> productosIds = new HashSet<>();
        for (var item : request.getDetalles()) {
            if (productosIds.contains(item.getIdProducto())) {
                throw new ConflictoException("No se puede repetir un producto en la misma orden");
            }
            productosIds.add(item.getIdProducto());
        }

        List<Producto> productos = productoRepository.bloquearPorIds(new ArrayList<>(productosIds));
        var productosMap = productos.stream().collect(java.util.stream.Collectors.toMap(Producto::getId, p -> p));

        OrdenVenta orden = new OrdenVenta();
        orden.setUsuario(usuario);
        orden.setFechaOrden(LocalDateTime.now());
        orden.setEstado(EstadoOrden.PENDIENTE);

        for (var item : request.getDetalles()) {
            Producto producto = productosMap.get(item.getIdProducto());
            if (producto == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado con id " + item.getIdProducto());
            }
            if (Boolean.FALSE.equals(producto.getActivo())) {
                throw new SolicitudInvalidaException("No se puede vender un producto inactivo");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new SolicitudInvalidaException("La cantidad debe ser mayor a 0");
            }

            Long stock = movimientoInventarioRepository.calcularStock(producto.getId());
            if (stock == null || stock < item.getCantidad()) {
                throw new ConflictoException("Stock insuficiente para el producto " + producto.getNombre());
            }

            BigDecimal descuento = item.getDescuento() == null ? BigDecimal.ZERO : item.getDescuento();
            BigDecimal precioUnitario = producto.getPrecioVenta();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())).subtract(descuento);
            if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                throw new SolicitudInvalidaException("El descuento no puede superar el valor de la línea");
            }

            DetalleOrdenVenta detalle = new DetalleOrdenVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.setSubtotal(subtotal);
            orden.agregarDetalle(detalle);
        }

        ordenVentaRepository.save(orden);

        for (DetalleOrdenVenta detalle : orden.getDetalles()) {
            inventarioService.registrarSalidaVenta(orden, detalle, usuario);
        }

        return toDTO(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenVentaDTO obtenerPorId(Integer id, String correoAutenticado) {
        OrdenVenta orden = buscarPorId(id);
        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        boolean esAdminOEmpleado = usuario.getRol() == Rol.EMPLEADO || usuario.getRol() == Rol.ADMINISTRADOR;
        boolean esDueño = orden.getUsuario() != null && orden.getUsuario().getId().equals(usuario.getId());
        if (!esDueño && !esAdminOEmpleado) {
            throw new AccessDeniedException("No tiene permiso para ver esta orden");
        }
        return toDTO(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenVentaDTO> obtenerTodos(String correoAutenticado) {
        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        boolean esAdminOEmpleado = usuario.getRol() == Rol.EMPLEADO || usuario.getRol() == Rol.ADMINISTRADOR;
        if (esAdminOEmpleado) {
            return ordenVentaRepository.findAll().stream().map(this::toDTO).toList();
        }
        return ordenVentaRepository.findAll().stream()
                .filter(o -> o.getUsuario() != null && o.getUsuario().getId().equals(usuario.getId()))
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public OrdenVentaDTO cancelar(Integer id, String correoAutenticado) {
        OrdenVenta orden = buscarPorId(id);
        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        boolean esAdminOEmpleado = usuario.getRol() == Rol.EMPLEADO || usuario.getRol() == Rol.ADMINISTRADOR;
        boolean esDueño = orden.getUsuario() != null && orden.getUsuario().getId().equals(usuario.getId());
        if (!esDueño && !esAdminOEmpleado) {
            throw new AccessDeniedException("No tiene permiso para cancelar esta orden");
        }
        if (orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new SolicitudInvalidaException("La orden ya está cancelada");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new SolicitudInvalidaException("Solo se pueden cancelar órdenes en estado PENDIENTE");
        }

        for (DetalleOrdenVenta detalle : orden.getDetalles()) {
            inventarioService.registrarAnulacionVenta(orden, detalle, usuario);
        }

        orden.setEstado(EstadoOrden.CANCELADA);
        ordenVentaRepository.save(orden);
        return toDTO(orden);
    }

    @Override
    @Transactional
    public OrdenVentaDTO entregar(Integer id) {
        OrdenVenta orden = buscarPorId(id);
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new SolicitudInvalidaException("Solo se pueden entregar órdenes en estado PENDIENTE");
        }
        orden.setEstado(EstadoOrden.ENTREGADA);
        ordenVentaRepository.save(orden);
        return toDTO(orden);
    }

    private OrdenVenta buscarPorId(Integer id) {
        return ordenVentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("OrdenVenta no encontrada con id " + id));
    }

    private OrdenVentaDTO toDTO(OrdenVenta orden) {
        OrdenVentaDTO dto = new OrdenVentaDTO();
        dto.setId(orden.getId());
        dto.setIdUsuario(orden.getUsuario() != null ? orden.getUsuario().getId() : null);
        dto.setNombreUsuario(orden.getUsuario() != null ? orden.getUsuario().getNombre() : null);
        dto.setCorreoUsuario(orden.getUsuario() != null ? orden.getUsuario().getCorreo() : null);
        dto.setFechaOrden(orden.getFechaOrden());
        dto.setEstado(orden.getEstado());

        List<DetalleOrdenVentaDTO> detallesDto = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        if (orden.getDetalles() != null) {
            for (DetalleOrdenVenta detalle : orden.getDetalles()) {
                DetalleOrdenVentaDTO d = new DetalleOrdenVentaDTO();
                d.setId(detalle.getId());
                d.setIdProducto(detalle.getProducto() != null ? detalle.getProducto().getId() : null);
                d.setNombreProducto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null);
                d.setCantidad(detalle.getCantidad());
                d.setPrecioUnitario(detalle.getPrecioUnitario());
                d.setDescuento(detalle.getDescuento());
                d.setSubtotal(detalle.getSubtotal());
                detallesDto.add(d);
                if (detalle.getSubtotal() != null) {
                    total = total.add(detalle.getSubtotal());
                }
            }
        }
        dto.setDetalles(detallesDto);
        dto.setTotal(total);
        return dto;
    }
}
