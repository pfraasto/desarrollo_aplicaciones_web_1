package pe.cibertec.desarrollo_aplicaciones_web_1.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.SeguridadModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.SeguridadService;
import pe.cibertec.desarrollo_aplicaciones_web_1.presentation.dto.LoginRequestDto;
import pe.cibertec.desarrollo_aplicaciones_web_1.presentation.dto.LoginResponseDto;
import pe.cibertec.desarrollo_aplicaciones_web_1.presentation.dto.RefreshTokenRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/api/auth")
public class AuthController {

    @Value("${security.jwt.access-token.expiration}")
    private long duracionTokenSegundos;

    private final SeguridadService seguridadService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        SeguridadModel seguridad = seguridadService.autenticacion(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(new LoginResponseDto(
                seguridad.getToken(),
                seguridad.getRefresh(),
                (duracionTokenSegundos / 1000)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        SeguridadModel seguridad = seguridadService.refrescar(request.getRefreshToken());

        return ResponseEntity.ok(new LoginResponseDto(
                seguridad.getToken(),
                seguridad.getRefresh(),
                (duracionTokenSegundos / 1000)
        ));
    }
}

