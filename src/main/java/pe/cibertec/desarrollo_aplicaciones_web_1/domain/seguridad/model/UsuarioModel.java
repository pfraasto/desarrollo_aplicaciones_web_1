package pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

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
