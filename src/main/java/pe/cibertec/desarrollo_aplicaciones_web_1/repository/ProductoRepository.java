package pe.cibertec.desarrollo_aplicaciones_web_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
