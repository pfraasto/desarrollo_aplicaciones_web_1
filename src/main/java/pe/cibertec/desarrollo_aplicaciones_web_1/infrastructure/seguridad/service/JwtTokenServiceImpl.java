package pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.seguridad.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.RolModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.model.UsuarioModel;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.service.TokenService;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements TokenService {

    @Value("${security.jwt.secret}")
    private String claveSecreta;

    @Value("${security.jwt.access-token.expiration}")
    private long expiracionTokenAccesoMilisegundos;

    @Value("${security.jwt.refresh-token.expiration}")
    private long expiracionTokenRefrescoMilisegundos;

    /**
     * Genera un Access Token con claims personalizados del usuario.
     */
    @Override
    public String generarTokenAcceso(UsuarioModel usuario) {
        Map<String, Object> claims = construirClaimsDesdeUsuario(usuario);
        return generarToken(claims, usuario.getUsername(), expiracionTokenAccesoMilisegundos);
    }

    /**
     * Genera un Refresh Token simple con solo el subject.
     */
    @Override
    public String generarTokenRefresco(UsuarioModel usuario) {
        return generarToken(Collections.emptyMap(), usuario.getUsername(), expiracionTokenRefrescoMilisegundos);
    }

    /**
     * Extrae el `sub` (sujeto) del token, que en este caso es el username.
     */
    @Override
    public String extraerUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Verifica que el token sea válido y esté bien firmado.
     */
    @Override
    public boolean esTokenValido(String token) {
        try {
            Jwts.parser()
                    .verifyWith(obtenerClaveFirma())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // --- Métodos privados ---
    private Map<String, Object> construirClaimsDesdeUsuario(UsuarioModel usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", usuario.getNombre() + " " + usuario.getApellido());
        claims.put("roles", usuario.getRoles()
                .stream()
                .map(RolModel::getNombre)
                .toList());
        return claims;
    }

    private String generarToken(Map<String, Object> claims, String subject, long expiracionMilisegundos) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiracionMilisegundos))
                .signWith(obtenerClaveFirma())
                .compact();
    }

    private SecretKey obtenerClaveFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(claveSecreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveFirma())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraerTodosLosClaims(token));
    }
}
