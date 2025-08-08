package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeguridadModel {
    private String token;
    private String refresh;
}
