package pe.cibertec.desarrollo_aplicaciones_web_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
