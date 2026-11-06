package com.unicartagena.muebles_ana_api.application.controller;

import com.unicartagena.muebles_ana_api.application.dto.OrdenVentaDTO;
import com.unicartagena.muebles_ana_api.application.request.OrdenVentaRequest;
import com.unicartagena.muebles_ana_api.application.service.OrdenVentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes-venta")
@RequiredArgsConstructor
public class OrdenVentaController {

    private final OrdenVentaService ordenVentaService;

    @PostMapping
    public ResponseEntity<OrdenVentaDTO> crear(@Valid @RequestBody OrdenVentaRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenVentaService.crear(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<OrdenVentaDTO>> obtenerTodos(Authentication authentication) {
        return ResponseEntity.ok(ordenVentaService.obtenerTodos(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenVentaDTO> obtenerPorId(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(ordenVentaService.obtenerPorId(id, authentication.getName()));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenVentaDTO> cancelar(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(ordenVentaService.cancelar(id, authentication.getName()));
    }

    @PostMapping("/{id}/entregar")
    public ResponseEntity<OrdenVentaDTO> entregar(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenVentaService.entregar(id));
    }
}
