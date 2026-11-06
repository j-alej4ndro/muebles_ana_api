package com.unicartagena.muebles_ana_api.application.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @NonNull
    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @NonNull
    @NotBlank
    @Column(nullable = false, length = 100, unique = true)
    private String correo;

    @NonNull
    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(length = 30, unique = true)
    private String documento;

    @Column(length = 30)
    private String telefono;

    @Column(length = 150)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
}
