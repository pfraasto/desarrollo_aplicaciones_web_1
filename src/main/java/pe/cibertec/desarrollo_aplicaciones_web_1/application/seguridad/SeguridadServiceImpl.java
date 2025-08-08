package pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.exception.CredencialesInvalidasException;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.SeguridadService;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.TokenService;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.configuration.seguridad.CustomUserDetails;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.seguridad.service.CustomUserDetailsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeguridadServiceImpl implements SeguridadService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public SeguridadModel autenticacion(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception ex) {
            log.warn("Autenticación fallida para el usuario '{}'", username, ex);
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        return generarTokensDesdeUsername(username);
    }

    @Override
    public SeguridadModel refrescar(String refreshToken) {
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
        String refreshToken = tokenService.generarTokenRefresco(usuario);

        return SeguridadModel.builder()
                .token(accessToken)
                .refresh(refreshToken)
                .build();
    }
}
