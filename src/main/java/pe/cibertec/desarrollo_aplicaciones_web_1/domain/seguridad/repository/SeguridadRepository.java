package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.repository;

import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;

import java.util.Optional;

public interface SeguridadRepository {
    Optional<UsuarioModel> usuarioPorUserName(String username);

    void guardarToken(String token);

    String obtenerTokenCache(String username);
}
