package com.devops.tutorial.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.devops.tutorial.model.ReglaAsignacion;

public interface ReglaAsignacionRepository extends JpaRepository<ReglaAsignacion, Long> {

    @Query(value = "SELECT * FROM regla_asignacion WHERE limite_inferior <= :montoOperacion AND limite_superior >= :montoOperacion", nativeQuery = true)
    ReglaAsignacion findByMontoOperacion(@Param("montoOperacion") Double montoOperacion);
}
