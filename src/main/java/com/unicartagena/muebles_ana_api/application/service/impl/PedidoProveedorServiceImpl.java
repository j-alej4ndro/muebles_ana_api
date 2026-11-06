package com.unicartagena.muebles_ana_api.application.service.impl;

import com.unicartagena.muebles_ana_api.application.dto.DetallePedidoProveedorDTO;
import com.unicartagena.muebles_ana_api.application.dto.PedidoProveedorDTO;
import com.unicartagena.muebles_ana_api.application.entities.*;
import com.unicartagena.muebles_ana_api.application.repositories.PedidoProveedorRepository;
import com.unicartagena.muebles_ana_api.application.repositories.ProductoRepository;
import com.unicartagena.muebles_ana_api.application.repositories.ProveedorRepository;
import com.unicartagena.muebles_ana_api.application.repositories.UsuarioRepository;
import com.unicartagena.muebles_ana_api.application.request.PedidoProveedorRequest;
import com.unicartagena.muebles_ana_api.application.service.InventarioService;
import com.unicartagena.muebles_ana_api.application.service.PedidoProveedorService;
import com.unicartagena.muebles_ana_api.base.exceptions.ConflictoException;
import com.unicartagena.muebles_ana_api.base.exceptions.RecursoNoEncontradoException;
import com.unicartagena.muebles_ana_api.base.exceptions.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
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
public class PedidoProveedorServiceImpl implements PedidoProveedorService {

    private final PedidoProveedorRepository pedidoProveedorRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    @Override
    @Transactional
    public PedidoProveedorDTO crear(PedidoProveedorRequest request, String correoAutenticado) {
        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id " + request.getIdProveedor()));
        if (Boolean.FALSE.equals(proveedor.getActivo())) {
            throw new SolicitudInvalidaException("No se puede crear un pedido a un proveedor inactivo");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new SolicitudInvalidaException("El pedido debe tener al menos un detalle");
        }

        Set<Integer> productosIds = new HashSet<>();
        List<DetallePedidoProveedor> detalles = new ArrayList<>();

        for (var item : request.getDetalles()) {
            if (productosIds.contains(item.getIdProducto())) {
                throw new ConflictoException("No se puede repetir un producto en el mismo pedido");
            }
            productosIds.add(item.getIdProducto());

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + item.getIdProducto()));
            if (Boolean.FALSE.equals(producto.getActivo())) {
                throw new SolicitudInvalidaException("No se puede pedir un producto inactivo");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new SolicitudInvalidaException("La cantidad debe ser mayor a 0");
            }
            if (item.getPrecioUnitario() == null || item.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new SolicitudInvalidaException("El precio unitario no puede ser negativo");
            }

            DetallePedidoProveedor detalle = new DetallePedidoProveedor();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
            detalles.add(detalle);
        }

        PedidoProveedor pedido = new PedidoProveedor();
        pedido.setProveedor(proveedor);
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        for (DetallePedidoProveedor detalle : detalles) {
            pedido.agregarDetalle(detalle);
        }

        pedidoProveedorRepository.save(pedido);
        return toDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoProveedorDTO obtenerPorId(Integer id) {
        return toDTO(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoProveedorDTO> obtenerTodos() {
        return pedidoProveedorRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public PedidoProveedorDTO recibir(Integer id, String correoAutenticado) {
        PedidoProveedor pedido = buscarPorId(id);
        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new SolicitudInvalidaException("Solo se pueden recibir pedidos en estado PENDIENTE");
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new SolicitudInvalidaException("El pedido no tiene detalles");
        }

        for (DetallePedidoProveedor detalle : pedido.getDetalles()) {
            inventarioService.registrarEntradaCompra(pedido, detalle, usuario);
        }

        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedidoProveedorRepository.save(pedido);
        return toDTO(pedido);
    }

    @Override
    @Transactional
    public PedidoProveedorDTO cancelar(Integer id, String correoAutenticado) {
        PedidoProveedor pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new SolicitudInvalidaException("Solo se pueden cancelar pedidos en estado PENDIENTE");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoProveedorRepository.save(pedido);
        return toDTO(pedido);
    }

    private PedidoProveedor buscarPorId(Integer id) {
        return pedidoProveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("PedidoProveedor no encontrado con id " + id));
    }

    private PedidoProveedorDTO toDTO(PedidoProveedor pedido) {
        PedidoProveedorDTO dto = new PedidoProveedorDTO();
        dto.setId(pedido.getId());
        dto.setIdProveedor(pedido.getProveedor() != null ? pedido.getProveedor().getId() : null);
        dto.setNombreProveedor(pedido.getProveedor() != null ? pedido.getProveedor().getNombre() : null);
        dto.setIdUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getId() : null);
        dto.setNombreUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : null);
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());

        List<DetallePedidoProveedorDTO> detallesDto = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedidoProveedor detalle : pedido.getDetalles()) {
                DetallePedidoProveedorDTO d = new DetallePedidoProveedorDTO();
                d.setId(detalle.getId());
                d.setIdProducto(detalle.getProducto() != null ? detalle.getProducto().getId() : null);
                d.setNombreProducto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null);
                d.setCantidad(detalle.getCantidad());
                d.setPrecioUnitario(detalle.getPrecioUnitario());
                d.setSubtotal(detalle.getSubtotal());
                detallesDto.add(d);
            }
        }
        dto.setDetalles(detallesDto);
        return dto;
    }
}
