package com.unicartagena.muebles_ana_api.application.repositories;

import com.unicartagena.muebles_ana_api.application.entities.PedidoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoProveedorRepository extends JpaRepository<PedidoProveedor, Integer> {
}
