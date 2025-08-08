package pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.seguridad.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.cibertec.desarrollo_aplicaciones_web_1.domain.seguridad.repository.UsuarioRepository;
import pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.configuration.seguridad.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.usuarioPorUserName(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username: " + username));
    }
}
