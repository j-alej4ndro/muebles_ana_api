package com.unicartagena.muebles_ana_api.base.configs;

import com.unicartagena.muebles_ana_api.application.entities.Rol;
import com.unicartagena.muebles_ana_api.application.entities.Usuario;
import com.unicartagena.muebles_ana_api.application.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrap {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner bootstrapAdmin(org.springframework.core.env.Environment env) {
        return args -> {
            long adminsActivos = usuarioRepository.countByRolAndActivoTrue(Rol.ADMINISTRADOR);
            if (adminsActivos > 0) {
                return;
            }

            String correo = env.getProperty("app.admin.correo");
            String nombre = env.getProperty("app.admin.nombre", "Administrador");
            String password = env.getProperty("app.admin.password", "Admin1234");

            if (correo == null || correo.isBlank()) {
                return;
            }

            String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
            Usuario usuario = usuarioRepository.findByCorreo(correoNormalizado).orElse(null);

            if (usuario != null) {
                usuario.setRol(Rol.ADMINISTRADOR);
                usuario.setActivo(true);
                usuario.setPasswordHash(passwordEncoder.encode(password));
                if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
                    usuario.setNombre(nombre);
                }
                if (usuario.getFechaCreacion() == null) {
                    usuario.setFechaCreacion(LocalDateTime.now());
                }
                usuarioRepository.save(usuario);
                return;
            }

            Usuario admin = new Usuario(nombre, correoNormalizado, passwordEncoder.encode(password), Rol.ADMINISTRADOR);
            admin.setActivo(true);
            admin.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(admin);
        };
    }
}
