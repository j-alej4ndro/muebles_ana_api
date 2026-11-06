package com.unicartagena.muebles_ana_api.application.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer id;

    @NonNull
    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @NonNull
    @NotBlank
    @Column(nullable = false, length = 30, unique = true)
    private String documento;

    @Column(length = 100)
    private String contacto;

    @Column(length = 30)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(nullable = false)
    private Boolean activo = true;
}
