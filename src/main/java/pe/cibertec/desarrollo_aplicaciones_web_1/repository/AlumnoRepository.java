package pe.cibertec.desarrollo_aplicaciones_web_1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Alumno;

import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    @Query("SELECT a FROM Alumno a WHERE a.nombre LIKE %:nombre%")
    List<Alumno> buscarPorNombre(@Param("nombre") String nombre);

    Page<Alumno> findByNombreContaining(String nombre, Pageable pageable);
}
