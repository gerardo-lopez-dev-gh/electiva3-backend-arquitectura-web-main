package com.devops.tutorial.repository;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devops.tutorial.dto.ClienteBolsaPuntosDTO;
import com.devops.tutorial.model.BolsaPuntos;

public interface BolsaPuntosRepository extends JpaRepository<BolsaPuntos, Long> {

    @Query(
        nativeQuery = true,
        value = "SELECT b.* FROM bolsa_puntos b "+
                "left JOIN param_vencimiento pv ON b.param_vencimiento_id=pv.id "+
                "WHERE pv.fecha_fin  < :fechaCaducidad "+
                "AND b.saldo_puntos  > 0;"
    )
    List<BolsaPuntos> findBolsasPuntosVencidas(
        @Param("fechaCaducidad") Date ahora
    );

    @Query(
    nativeQuery = true,
    value = "SELECT distinct c.* " +
    "FROM bolsa_puntos bp " +
    "JOIN cliente c ON bp.cliente_id = c.id " +
    "JOIN param_vencimiento pv ON bp.param_vencimiento_id = pv.id " +
    "WHERE DATEDIFF(pv.fecha_fin, CURRENT_DATE) <= :dias " +
    "AND (:clienteId IS NULL OR bp.cliente_id = :clienteId)"
    )
    List<Object[]> findClientesConPuntosAVencer(
        @Param("dias") int dias,
        @Param("clienteId") BigInteger clienteId
    );
    
    @Query(
    nativeQuery = true,
    value = "SELECT c.id AS cliente_id, c.nombre, c.apellido, c.fecha_nacimiento, b.id AS bolsa_puntos_id, r.limite_superior, r.limite_inferior " +
            "FROM bolsa_puntos b " +
            "JOIN cliente c ON b.cliente_id = c.id " +
            "JOIN regla_asignacion r ON b.regla_asignacion_id = r.id " +
            "WHERE c.id = :clienteId"
    )
    List<Object> findClienteBolsaPuntosByClienteId(@Param("clienteId") Long clienteId);

    @Query(value = "SELECT bp.*, (bp.puntaje_asignado - bp.puntaje_utilizado) AS disponible FROM bolsa_puntos bp " + 
               "JOIN param_vencimiento pv ON bp.param_vencimiento_id = pv.id " +
               "WHERE bp.cliente_id = :clienteId " +
               "AND :fechaCarga BETWEEN pv.fecha_inicio AND pv.fecha_fin " +
               "HAVING disponible > 0 " +
               "ORDER BY bp.fecha_asignacion ASC", nativeQuery = true)
    List<Object[]> findPuntosDisponibles(@Param("clienteId") Long clienteId, @Param("fechaCarga") Date fechaCarga);
}
