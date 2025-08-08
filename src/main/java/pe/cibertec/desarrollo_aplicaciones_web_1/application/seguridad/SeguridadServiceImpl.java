package pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.usecase.AutenticarUsuarioUseCase;
import pe.cibertec.desarrollo_aplicaciones_web_1.application.seguridad.usecase.RefrescarTokenUseCase;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.SeguridadService;

@Service
@RequiredArgsConstructor
public class SeguridadServiceImpl implements SeguridadService {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final RefrescarTokenUseCase refrescarTokenUseCase;

    @Override
    public SeguridadModel autenticacion(String username, String password) {
        return autenticarUsuarioUseCase.ejecutar(username, password);
    }

    @Override
    public SeguridadModel refrescar(String refreshToken) {
        return refrescarTokenUseCase.ejecutar(refreshToken);
    }
}
