package pe.cibertec.desarrollo_aplicaciones_web_1.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}
