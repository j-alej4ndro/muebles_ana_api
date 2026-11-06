package com.unicartagena.muebles_ana_api.application.repositories;

import com.unicartagena.muebles_ana_api.application.entities.DetalleOrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleOrdenVentaRepository extends JpaRepository<DetalleOrdenVenta, Integer> {
}
