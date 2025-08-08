package pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.configuration.seguridad;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para JWT.
 * Se cargan desde el prefijo security.jwt en application.yml
 */
@Data
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    /**
     * Clave secreta para firmar tokens. Puede estar en Base64 si secretBase64=true
     */
    private String secret;
    /**
     * Expiración del access token en milisegundos
     */
    private long accessTokenExpiration;
    /**
     * Expiración del refresh token en milisegundos
     */
    private long refreshTokenExpiration;
}
