package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RolModel {
    private String nombre;
    private String descripcion;
}
