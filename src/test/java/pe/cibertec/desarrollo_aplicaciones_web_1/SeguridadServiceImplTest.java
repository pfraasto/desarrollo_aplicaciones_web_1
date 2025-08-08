package pe.cibertec.desarrollo_aplicaciones_web_1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.SeguridadServiceImpl;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.TokenService;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.configuration.seguridad.CustomUserDetails;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.seguridad.service.CustomUserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeguridadServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private SeguridadServiceImpl seguridadService;

    @Test
    void autenticacion_credencialesValidas_retornaTokens() {
        // Arrange
        String username = "usuario";
        String password = "password";
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        UsuarioModel usuario = mock(UsuarioModel.class);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);
        when(tokenService.generarTokenAcceso(usuario)).thenReturn("access-token");
        when(tokenService.generarTokenRefresco(usuario)).thenReturn("refresh-token");

        // Act
        SeguridadModel resultado = seguridadService.autenticacion(username, password);

        // Assert
        assertNotNull(resultado);
        assertEquals("access-token", resultado.getToken());
        assertEquals("refresh-token", resultado.getRefresh());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
