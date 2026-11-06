package com.unicartagena.muebles_ana_api.application.controller;

import com.unicartagena.muebles_ana_api.application.dto.PedidoProveedorDTO;
import com.unicartagena.muebles_ana_api.application.request.PedidoProveedorRequest;
import com.unicartagena.muebles_ana_api.application.service.PedidoProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos-proveedor")
@RequiredArgsConstructor
public class PedidoProveedorController {

    private final PedidoProveedorService pedidoProveedorService;

    @PostMapping
    public ResponseEntity<PedidoProveedorDTO> crear(@Valid @RequestBody PedidoProveedorRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoProveedorService.crear(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PedidoProveedorDTO>> obtenerTodos() {
        return ResponseEntity.ok(pedidoProveedorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoProveedorDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoProveedorService.obtenerPorId(id));
    }

    @PostMapping("/{id}/recibir")
    public ResponseEntity<PedidoProveedorDTO> recibir(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(pedidoProveedorService.recibir(id, authentication.getName()));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PedidoProveedorDTO> cancelar(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(pedidoProveedorService.cancelar(id, authentication.getName()));
    }
}
