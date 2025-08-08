package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service;

import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;

public interface SeguridadService {
    SeguridadModel autenticacion(String username, String password);

    SeguridadModel refrescar(String token);
}
