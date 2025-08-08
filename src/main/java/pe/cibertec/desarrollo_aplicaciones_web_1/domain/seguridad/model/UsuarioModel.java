package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Modelo de dominio para el Usuario.
 * Campos relevantes:
 * - username: Identificador único de autenticación.
 * - email: Correo de contacto (no se usa para autenticación en este contexto).
 * - roles: Conjunto de roles que determinan autorizaciones.
 * Nota: Es un objeto de dominio y debe permanecer independiente de frameworks.
 */
@Data
@Builder
public class UsuarioModel {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private boolean activo;
    private Set<RolModel> roles;
}
