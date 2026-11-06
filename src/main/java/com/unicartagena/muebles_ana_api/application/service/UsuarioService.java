package com.unicartagena.muebles_ana_api.application.service;

import com.unicartagena.muebles_ana_api.application.dto.LoginResponse;
import com.unicartagena.muebles_ana_api.application.dto.UsuarioDTO;
import com.unicartagena.muebles_ana_api.application.entities.Rol;
import com.unicartagena.muebles_ana_api.application.request.*;

import java.util.List;

public interface UsuarioService {

    UsuarioDTO registrar(RegistroRequest request);
    LoginResponse login(LoginRequest request);

    UsuarioDTO crear(AdminUsuarioCreateRequest request);
    UsuarioDTO obtenerPorId(Integer id);
    List<UsuarioDTO> obtenerTodos();
    UsuarioDTO actualizar(Integer id, UsuarioUpdateRequest request);
    UsuarioDTO cambiarRol(Integer id, CambioRolRequest request);
    UsuarioDTO cambiarEstado(Integer id, EstadoRequest request);
    void eliminar(Integer id);

    UsuarioDTO obtenerPerfil(String correo);
    UsuarioDTO actualizarPerfil(String correo, PerfilRequest request);
}
