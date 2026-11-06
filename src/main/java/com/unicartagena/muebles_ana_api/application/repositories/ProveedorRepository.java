package com.unicartagena.muebles_ana_api.application.repositories;

import com.unicartagena.muebles_ana_api.application.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Integer id);

}
