package com.devops.tutorial.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.devops.tutorial.model.ParamVencimiento;

public interface ParamVencimientoRepository extends JpaRepository<ParamVencimiento, Long> {

    @Query(value = "SELECT * FROM param_vencimiento ORDER BY id ASC LIMIT 1", nativeQuery = true)
    ParamVencimiento findFirstOrderedById();
}
