package pe.cibertec.desarrollo_aplicaciones_web_1;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.SeguridadServiceImpl;
import pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.usecase.AutenticarUsuarioUseCase;
import pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.usecase.RefrescarTokenUseCase;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeguridadServiceImplTest {

    @Mock
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Mock
    private RefrescarTokenUseCase refrescarTokenUseCase;

    @InjectMocks
    private SeguridadServiceImpl seguridadService;


    @Test
    void generar_llave() {
        // Generar una nueva clave HMAC-SHA512
        SecretKey key = Jwts.SIG.HS512.key().build();
        // Obtener en base64url para guardar
        String secretBase64Url = Encoders.BASE64URL.encode(key.getEncoded());
        System.out.println("secretBase64Url: " + secretBase64Url);
        assertNotNull(secretBase64Url);
    }

    @Test
    void autenticacion_credencialesValidas_retornaTokens() {
        // Arrange
        String username = "usuario";
        String password = "password";
        SeguridadModel esperado = SeguridadModel.builder().token("access-token").refresh("refresh-token").build();
        when(autenticarUsuarioUseCase.ejecutar(username, password)).thenReturn(esperado);

        // Act
        SeguridadModel resultado = seguridadService.autenticacion(username, password);

        // Assert
        assertNotNull(resultado);
        assertEquals("access-token", resultado.getToken());
        assertEquals("refresh-token", resultado.getRefresh());
        verify(autenticarUsuarioUseCase).ejecutar(username, password);
        verifyNoInteractions(refrescarTokenUseCase);
    }
}
