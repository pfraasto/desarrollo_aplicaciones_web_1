package pe.cibertec.desarrollo_aplicaciones_web_1.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de solicitud para login.
 */
@Data
public class LoginRequestDto {
    /** Nombre de usuario o identificador de acceso */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;
    /** Contraseña del usuario */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
