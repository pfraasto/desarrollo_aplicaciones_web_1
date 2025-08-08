# Guía de Implementación de Spring Security

Esta guía paso a paso te ayudará a implementar Spring Security con autenticación JWT en tu proyecto Spring Boot. Está diseñada para estudiantes que están aprendiendo Spring Security.

## Índice
1. [Configuración del Proyecto](#1-configuración-del-proyecto)
2. [Modelos de Dominio](#2-modelos-de-dominio)
3. [Configuración de Seguridad](#3-configuración-de-seguridad)
4. [Implementación de JWT](#4-implementación-de-jwt)
5. [Servicios de Autenticación](#5-servicios-de-autenticación)
6. [Controladores](#6-controladores)
7. [Pruebas](#7-pruebas)

## 1. Configuración del Proyecto

### Paso 1: Crear un proyecto Spring Boot

Crea un nuevo proyecto Spring Boot con las siguientes dependencias:
- Spring Web
- Spring Security
- Spring Data JPA
- Lombok
- MySQL Connector (o tu base de datos preferida)

### Paso 2: Configurar el archivo pom.xml

Añade las dependencias de JWT:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
</dependency>
```

### Paso 3: Configurar application.yml

Configura la conexión a la base de datos y las propiedades de JWT:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tu_base_de_datos
    username: tu_usuario
    password: tu_contraseña
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

security:
  jwt:
    secret: tu_clave_secreta_muy_larga_y_segura_base64
    access-token:
      expiration: 900000  # 15 minutos en milisegundos
    refresh-token:
      expiration: 604800000  # 7 días en milisegundos
```

## 2. Modelos de Dominio

### Paso 1: Crear modelos de dominio

Crea los modelos de dominio para representar usuarios y roles:

**RolModel.java**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolModel {
    private Long id;
    private String nombre;
}
```

**UsuarioModel.java**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioModel {
    private Long id;
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private boolean activo;
    private List<RolModel> roles;
}
```

**SeguridadModel.java**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguridadModel {
    private String token;
    private String refresh;
}
```

### Paso 2: Crear entidades JPA

Crea las entidades JPA correspondientes:

**RolEntity.java**
```java
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nombre;
}
```

**UsuarioEntity.java**
```java
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
    
    private String password;
    private String nombre;
    private String apellido;
    private boolean activo;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<RolEntity> roles;
}
```

## 3. Configuración de Seguridad

### Paso 1: Crear CustomUserDetails

Crea una clase que implemente UserDetails para adaptar tu modelo de usuario:

```java
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final transient UsuarioModel usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles()
                .stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .toList();
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo();
    }
}
```

### Paso 2: Crear CustomUserDetailsService

Implementa UserDetailsService para cargar usuarios desde tu repositorio:

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SeguridadRepository seguridadRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return seguridadRepository.usuarioPorUserName(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username: " + username));
    }
}
```

### Paso 3: Configurar SecurityConfig

Configura Spring Security:

```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## 4. Implementación de JWT

### Paso 1: Crear interfaz TokenService

Define una interfaz para las operaciones con tokens:

```java
public interface TokenService {
    String generarTokenAcceso(UsuarioModel usuario);

    String generarTokenRefresco(UsuarioModel usuario);

    String extraerUsuario(String token);

    boolean esTokenValido(String token);
}
```

### Paso 2: Implementar JwtTokenServiceImpl

Implementa la lógica para generar y validar tokens JWT:

```java
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements TokenService {

    @Value("${security.jwt.secret}")
    private String claveSecreta;

    @Value("${security.jwt.access-token.expiration}")
    private long expiracionTokenAccesoMilisegundos;

    @Value("${security.jwt.refresh-token.expiration}")
    private long expiracionTokenRefrescoMilisegundos;

    @Override
    public String generarTokenAcceso(UsuarioModel usuario) {
        Map<String, Object> claims = construirClaimsDesdeUsuario(usuario);
        return generarToken(claims, usuario.getUsername(), expiracionTokenAccesoMilisegundos);
    }

    @Override
    public String generarTokenRefresco(UsuarioModel usuario) {
        return generarToken(Collections.emptyMap(), usuario.getUsername(), expiracionTokenRefrescoMilisegundos);
    }

    @Override
    public String extraerUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

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
```

### Paso 3: Crear JwtAuthenticationFilter

Implementa un filtro para procesar tokens JWT en cada solicitud:

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            if (tokenService.esTokenValido(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = tokenService.extraerUsuario(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            logger.warn("Autenticación JWT fallida: {}", ex);
        }

        filterChain.doFilter(request, response);
    }
}
```

## 5. Servicios de Autenticación

### Paso 1: Crear interfaz SeguridadService

Define una interfaz para las operaciones de autenticación:

```java
public interface SeguridadService {
    SeguridadModel autenticacion(String username, String password);

    SeguridadModel refrescar(String token);
}
```

### Paso 2: Implementar SeguridadServiceImpl

Implementa la lógica de autenticación:

```java
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
```

## 6. Controladores

### Paso 1: Crear DTOs

Crea objetos de transferencia de datos para las solicitudes y respuestas:

**LoginRequestDto.java**
```java
@Data
public class LoginRequestDto {
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
```

**LoginResponseDto.java**
```java
@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String refreshToken;
    private long expiresIn;
}
```

**RefreshTokenRequestDto.java**
```java
@Data
public class RefreshTokenRequestDto {
    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}
```

### Paso 2: Implementar AuthController

Crea un controlador para manejar la autenticación:

```java
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/api/auth")
public class AuthController {

    @Value("${security.jwt.token-validity-seconds:900}")
    private long duracionTokenSegundos;

    private final SeguridadService seguridadService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        SeguridadModel seguridad = seguridadService.autenticacion(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(new LoginResponseDto(
                seguridad.getToken(),
                seguridad.getRefresh(),
                duracionTokenSegundos
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        SeguridadModel seguridad = seguridadService.refrescar(request.getRefreshToken());

        return ResponseEntity.ok(new LoginResponseDto(
                seguridad.getToken(),
                seguridad.getRefresh(),
                duracionTokenSegundos
        ));
    }
}
```

## 7. Pruebas

### Paso 1: Probar el endpoint de login

Usa Postman o cualquier cliente HTTP para probar el endpoint de login:

```http
POST /public/api/auth/login
Content-Type: application/json

{
  "username": "usuario1",
  "password": "contraseña123"
}
```

### Paso 2: Probar el acceso a recursos protegidos

Usa el token obtenido para acceder a recursos protegidos:

```http
GET /api/recurso-protegido
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Paso 3: Probar el refresco de tokens

Cuando el token de acceso expire, usa el token de refresco para obtener nuevos tokens:

```http
POST /public/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Conclusión

Has implementado con éxito Spring Security con autenticación JWT en tu aplicación Spring Boot. Esta implementación proporciona:

1. Autenticación segura basada en tokens JWT
2. Control de acceso basado en roles
3. Gestión de sesiones sin estado (stateless)
4. Refresco de tokens para mantener la sesión del usuario

Recuerda que esta es una implementación básica y puede requerir ajustes adicionales según tus necesidades específicas, como:

- Manejo de excepciones más detallado
- Validación adicional de tokens
- Revocación de tokens
- Implementación de listas negras para tokens invalidados
- Mejoras en la seguridad como rate limiting