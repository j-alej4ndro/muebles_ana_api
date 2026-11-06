package com.unicartagena.muebles_ana_api.application.repositories;

import com.unicartagena.muebles_ana_api.application.entities.Rol;
import com.unicartagena.muebles_ana_api.application.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
    boolean existsByCorreoAndIdNot(String correo, Integer id);
    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Integer id);

    long countByRolAndActivoTrue(Rol rol);

}
