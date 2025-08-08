package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
