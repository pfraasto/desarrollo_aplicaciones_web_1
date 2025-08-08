package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service;

import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;

public interface TokenService {
    String generarTokenAcceso(UsuarioModel usuario);

    String generarTokenRefresco(UsuarioModel usuario);

    String extraerUsuario(String token);

    boolean esTokenValido(String token);
}
