package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model;

import lombok.Builder;
import lombok.Data;

/**
 * Respuesta de seguridad que contiene los tokens emitidos.
 * - token: Access Token JWT.
 * - refresh: Refresh Token JWT.
 */
@Data
@Builder
public class SeguridadModel {
    private String token;
    private String refresh;
}
