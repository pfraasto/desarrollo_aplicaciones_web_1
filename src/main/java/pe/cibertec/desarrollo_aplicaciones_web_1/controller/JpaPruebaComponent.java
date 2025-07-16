package pe.cibertec.desarrollo_aplicaciones_web_1.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.cibertec.desarrollo_aplicaciones_web_1.service.JpaAvanzadoService;

@Component
@RequiredArgsConstructor
public class JpaPruebaComponent {
    private final JpaAvanzadoService jpaAvanzadoService;

    @PostConstruct
    public void ejecutar() {
        jpaAvanzadoService.demostracionCompleta();
    }
}
