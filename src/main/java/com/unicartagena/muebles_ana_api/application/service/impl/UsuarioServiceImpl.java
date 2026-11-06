package com.unicartagena.muebles_ana_api.application.service.impl;

import com.unicartagena.muebles_ana_api.application.dto.LoginResponse;
import com.unicartagena.muebles_ana_api.application.dto.UsuarioDTO;
import com.unicartagena.muebles_ana_api.application.entities.Rol;
import com.unicartagena.muebles_ana_api.application.entities.Usuario;
import com.unicartagena.muebles_ana_api.application.repositories.UsuarioRepository;
import com.unicartagena.muebles_ana_api.application.request.*;
import com.unicartagena.muebles_ana_api.application.service.UsuarioService;
import com.unicartagena.muebles_ana_api.base.exceptions.ConflictoException;
import com.unicartagena.muebles_ana_api.base.exceptions.RecursoNoEncontradoException;
import com.unicartagena.muebles_ana_api.base.exceptions.SolicitudInvalidaException;
import com.unicartagena.muebles_ana_api.base.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        String correo = normalizarCorreo(request.getCorreo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictoException("El correo '" + correo + "' ya está registrado");
        }
        String documento = normalizarDocumento(request.getDocumento());
        validarDocumentoUnico(documento, null);

        Usuario usuario = new Usuario(request.getNombre(), correo, passwordEncoder.encode(request.getPassword()), Rol.CLIENTE);
        usuario.setDocumento(documento);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String correo = normalizarCorreo(request.getCorreo());
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new SolicitudInvalidaException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new SolicitudInvalidaException("Correo o contraseña incorrectos");
        }
        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new SolicitudInvalidaException("El usuario está inactivo");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());
        return new LoginResponse(token, usuario.getCorreo(), usuario.getRol().name());
    }

    @Override
    @Transactional
    public UsuarioDTO crear(AdminUsuarioCreateRequest request) {
        String correo = normalizarCorreo(request.getCorreo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictoException("El correo '" + correo + "' ya está registrado");
        }
        String documento = normalizarDocumento(request.getDocumento());
        validarDocumentoUnico(documento, null);

        Usuario usuario = new Usuario(request.getNombre(), correo, passwordEncoder.encode(request.getPassword()), request.getRol());
        usuario.setDocumento(documento);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Integer id) {
        return toDTO(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public UsuarioDTO actualizar(Integer id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorId(id);

        String correo = normalizarCorreo(request.getCorreo());
        if (usuarioRepository.existsByCorreoAndIdNot(correo, id)) {
            throw new ConflictoException("El correo '" + correo + "' ya está registrado");
        }

        String documento = normalizarDocumento(request.getDocumento());
        if (usuarioRepository.existsByDocumentoAndIdNot(documento, id)) {
            throw new ConflictoException("El documento '" + documento + "' ya está registrado");
        }

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(correo);
        if (tienePassword(request.getPassword())) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        usuario.setDocumento(documento);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO cambiarRol(Integer id, CambioRolRequest request) {
        Usuario usuario = buscarPorId(id);
        Rol nuevoRol = request.getRol();
        Rol rolActual = usuario.getRol();

        if (rolActual == Rol.ADMINISTRADOR && nuevoRol != Rol.ADMINISTRADOR) {
            validarQueNoEsUltimoAdministrador(usuario);
        }

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO cambiarEstado(Integer id, EstadoRequest request) {
        Usuario usuario = buscarPorId(id);
        Boolean nuevoEstado = request.getActivo();

        if (Boolean.FALSE.equals(nuevoEstado) && usuario.getRol() == Rol.ADMINISTRADOR) {
            validarQueNoEsUltimoAdministrador(usuario);
        }

        usuario.setActivo(nuevoEstado);
        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            validarQueNoEsUltimoAdministrador(usuario);
        }
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPerfil(String correo) {
        String normalizado = normalizarCorreo(correo);
        Usuario usuario = usuarioRepository.findByCorreo(normalizado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarPerfil(String correo, PerfilRequest request) {
        String normalizado = normalizarCorreo(correo);
        Usuario usuario = usuarioRepository.findByCorreo(normalizado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String correoNuevo = normalizarCorreo(request.getCorreo());
        if (usuarioRepository.existsByCorreoAndIdNot(correoNuevo, usuario.getId())) {
            throw new ConflictoException("El correo '" + correoNuevo + "' ya está registrado");
        }

        String documento = normalizarDocumento(request.getDocumento());
        if (usuarioRepository.existsByDocumentoAndIdNot(documento, usuario.getId())) {
            throw new ConflictoException("El documento '" + documento + "' ya está registrado");
        }

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(correoNuevo);
        if (tienePassword(request.getPassword())) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        usuario.setDocumento(documento);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    private Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
    }

    private void validarDocumentoUnico(String documento, Integer idExcluido) {
        if (documento == null) {
            return;
        }
        if (usuarioRepository.existsByDocumentoAndIdNot(documento, idExcluido)) {
            throw new ConflictoException("El documento '" + documento + "' ya está registrado");
        }
    }

    private void validarQueNoEsUltimoAdministrador(Usuario usuario) {
        long activos = usuarioRepository.countByRolAndActivoTrue(Rol.ADMINISTRADOR);
        if (activos <= 1) {
            throw new ConflictoException("No se puede quitar permisos o desactivar al último administrador activo");
        }
    }

    private boolean tienePassword(String password) {
        return password != null && !password.isBlank();
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        String limpio = documento.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    public UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());
        dto.setDocumento(usuario.getDocumento());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        return dto;
    }
}
