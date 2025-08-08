package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.exception;

/**
 * Excepción de dominio para credenciales inválidas.
 */
public class CredencialesInvalidasException extends DominioException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
