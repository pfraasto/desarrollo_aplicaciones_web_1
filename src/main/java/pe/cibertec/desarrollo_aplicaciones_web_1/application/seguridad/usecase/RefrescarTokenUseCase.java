package pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.exception.CredencialesInvalidasException;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.TokenService;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.configuration.seguridad.CustomUserDetails;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.seguridad.service.CustomUserDetailsService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefrescarTokenUseCase {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    public SeguridadModel ejecutar(String refreshToken) {
        if (!tokenService.esTokenValido(refreshToken)) {
            log.warn("Intento de uso de refresh token inválido: {}", refreshToken);
            throw new CredencialesInvalidasException("Refresh token inválido o expirado");
        }
        String username = tokenService.extraerUsuario(refreshToken);
        return generarTokensDesdeUsername(username);
    }

    private SeguridadModel generarTokensDesdeUsername(String username) {
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        UsuarioModel usuario = userDetails.getUsuario();

        String accessToken = tokenService.generarTokenAcceso(usuario);
        String nuevoRefreshToken = tokenService.generarTokenRefresco(usuario);

        return SeguridadModel.builder()
                .token(accessToken)
                .refresh(nuevoRefreshToken)
                .build();
    }
}
