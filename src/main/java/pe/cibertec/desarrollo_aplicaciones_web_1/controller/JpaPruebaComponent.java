package pe.cibertec.desarrollo_aplicaciones_web_1.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Alumno;
import pe.cibertec.desarrollo_aplicaciones_web_1.service.JpaAvanzadoService;

@Component
@RequiredArgsConstructor
public class JpaPruebaComponent {
    private final JpaAvanzadoService jpaAvanzadoService;

    @PostConstruct
    public void ejecutar() {
        Page<Alumno> paginado = jpaAvanzadoService.listarPaginado("Alumno", 0, 5);
        paginado.forEach(p -> System.out.println(p.getNombre()));
    }
}
