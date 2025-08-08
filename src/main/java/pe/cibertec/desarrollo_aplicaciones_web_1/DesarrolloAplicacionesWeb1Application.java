package pe.cibertec.desarrollo_aplicaciones_web_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio de Seguridad (Spring Boot).
 *
 * Propósito:
 * - Arrancar el contexto de Spring Boot para exponer endpoints de autenticación y autorización.
 * - No contiene lógica de negocio ni de infraestructura.
 *
 * Trazabilidad de arranque:
 * - La configuración de logs y banner se gestiona vía application.yml para mantener esta clase limpia.
 */
@SpringBootApplication
public class DesarrolloAplicacionesWeb1Application {

    public static void main(String[] args) {
        SpringApplication.run(DesarrolloAplicacionesWeb1Application.class, args);
    }

}
