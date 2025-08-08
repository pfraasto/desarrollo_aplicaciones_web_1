package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.repository;

import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;

import java.util.Optional;

/**
 * Contrato de repositorio del agregado Usuario.
 * Responsabilidades:
 * - Proveer acceso a los usuarios por criterios de consulta del dominio.
 * - Gestionar datos auxiliares del agregado (p. ej. cache de tokens si aplica al dominio).
 *
 * Este contrato NO debe exponer detalles de infraestructura.
 */
public interface UsuarioRepository {
    Optional<UsuarioModel> usuarioPorUserName(String username);

    void guardarToken(String token);

    String obtenerTokenCache(String username);
}
