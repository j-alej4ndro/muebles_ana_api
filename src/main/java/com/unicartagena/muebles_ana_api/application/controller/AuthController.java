package com.unicartagena.muebles_ana_api.application.controller;

import com.unicartagena.muebles_ana_api.application.dto.LoginResponse;
import com.unicartagena.muebles_ana_api.application.dto.UsuarioDTO;
import com.unicartagena.muebles_ana_api.application.request.LoginRequest;
import com.unicartagena.muebles_ana_api.application.request.RegistroRequest;
import com.unicartagena.muebles_ana_api.application.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}
