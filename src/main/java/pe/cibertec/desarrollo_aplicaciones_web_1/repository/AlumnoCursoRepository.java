package pe.cibertec.desarrollo_aplicaciones_web_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.AlumnoCurso;

import java.util.List;

public interface AlumnoCursoRepository extends JpaRepository<AlumnoCurso, Long> {
    /**
     * Consulta optimizada con JOIN FETCH para evitar N+1 problem
     * Carga AlumnoCurso junto con su Alumno relacionado en una sola consulta
     */
    @Query("SELECT ac FROM AlumnoCurso ac JOIN FETCH ac.alumno")
    List<AlumnoCurso> findAllWithAlumnos();
}
