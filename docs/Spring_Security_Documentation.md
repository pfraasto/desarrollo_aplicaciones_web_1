# Documentación de Spring Security

## Índice
1. [Introducción a Spring Security](#1-introducción-a-spring-security)
2. [Configuración de Spring Security](#2-configuración-de-spring-security)
   1. [Introducción](#21-introducción)
   2. [Configuración](#22-configuración)
   3. [Almacenamiento en memoria](#23-almacenamiento-en-memoria)
   4. [Personalización de la autenticación](#24-personalización-de-la-autenticación)
3. [Arquitectura del Proyecto](#3-arquitectura-del-proyecto)
4. [Clases y Métodos](#4-clases-y-métodos)
   1. [Capa de Configuración](#41-capa-de-configuración)
   2. [Capa de Dominio](#42-capa-de-dominio)
   3. [Capa de Aplicación](#43-capa-de-aplicación)
   4. [Capa de Infraestructura](#44-capa-de-infraestructura)
   5. [Capa de Presentación](#45-capa-de-presentación)
5. [Flujo de Autenticación](#5-flujo-de-autenticación)
6. [Ejemplos Prácticos](#6-ejemplos-prácticos)

## 1. Introducción a Spring Security

Spring Security es un framework potente y altamente personalizable de autenticación y control de acceso para aplicaciones Java. Es el estándar de facto para asegurar aplicaciones basadas en Spring.

**Características principales:**
- Autenticación y autorización completas
- Protección contra ataques como session fixation, clickjacking, cross-site request forgery, etc.
- Integración con Servlet API
- Integración opcional con Spring Web MVC
- Soporte para múltiples mecanismos de autenticación

En este proyecto, utilizamos Spring Security para implementar:
- Autenticación basada en JWT (JSON Web Tokens)
- Control de acceso basado en roles
- Protección de endpoints REST
- Gestión de sesiones stateless (sin estado)

## 2. Configuración de Spring Security

### 2.1. Introducción

Spring Security proporciona un sistema completo para proteger aplicaciones web. En este proyecto, hemos implementado una solución de seguridad basada en tokens JWT, que es ideal para APIs RESTful y aplicaciones modernas.

**¿Por qué JWT?**
- Stateless: No requiere almacenar sesiones en el servidor
- Escalable: Funciona bien en entornos distribuidos
- Portable: El mismo token puede ser utilizado por diferentes servicios
- Seguro: Contiene información firmada que no puede ser alterada

### 2.2. Configuración

La configuración de Spring Security se realiza principalmente en la clase `SecurityConfig`. Esta clase define cómo se protegen los endpoints, cómo se manejan las solicitudes HTTP, y cómo se configura la autenticación.

```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
    
    // Otros métodos de configuración...
}
```

**Elementos clave de la configuración:**

1. **CORS (Cross-Origin Resource Sharing)**: Permite solicitudes desde dominios específicos.
2. **CSRF (Cross-Site Request Forgery)**: Deshabilitado para APIs RESTful con autenticación basada en tokens.
3. **Gestión de Sesiones**: Configurada como STATELESS para no mantener estado de sesión en el servidor.
4. **Autorización de Solicitudes**: Define qué URLs son públicas y cuáles requieren autenticación.
5. **Filtro JWT**: Añade un filtro personalizado para procesar tokens JWT antes del filtro estándar de autenticación.

### 2.3. Almacenamiento en memoria

Spring Security permite almacenar usuarios en memoria, lo cual es útil para pruebas y desarrollo. Sin embargo, en este proyecto utilizamos un enfoque más robusto con almacenamiento en base de datos.

**Ejemplo de configuración en memoria (no utilizado en este proyecto):**

```java
@Bean
public InMemoryUserDetailsManager userDetailsService() {
    UserDetails user = User.withDefaultPasswordEncoder()
        .username("user")
        .password("password")
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
}
```

En lugar de esto, nuestro proyecto utiliza:
- Entidades JPA para representar usuarios y roles
- Repositorios para acceder a la base de datos
- Un servicio personalizado (`CustomUserDetailsService`) que implementa la interfaz `UserDetailsService` de Spring Security

### 2.4. Personalización de la autenticación

La personalización de la autenticación en Spring Security se puede realizar en varios niveles. En este proyecto, hemos personalizado:

1. **Servicio de Detalles de Usuario**: Implementamos `CustomUserDetailsService` para cargar usuarios desde nuestra base de datos.

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

2. **Detalles de Usuario Personalizados**: Creamos `CustomUserDetails` para adaptar nuestro modelo de dominio a la interfaz `UserDetails` de Spring Security.

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
    
    // Otros métodos implementados...
}
```

3. **Filtro de Autenticación JWT**: Implementamos `JwtAuthenticationFilter` para procesar tokens JWT en cada solicitud.

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
        // Lógica para extraer y validar el token JWT
        // Si es válido, establece la autenticación en el SecurityContext
    }
}
```

4. **Servicio de Tokens**: Implementamos `JwtTokenServiceImpl` para generar y validar tokens JWT.

## 3. Arquitectura del Proyecto

Este proyecto sigue una arquitectura limpia (Clean Architecture) con las siguientes capas:

1. **Capa de Dominio**: Contiene las entidades de negocio, interfaces de repositorios y servicios.
   - `domain.seguridad.model`: Modelos de dominio (UsuarioModel, RolModel, etc.)
   - `domain.seguridad.service`: Interfaces de servicios (SeguridadService, TokenService)
   - `domain.seguridad.repository`: Interfaces de repositorios (SeguridadRepository)

2. **Capa de Aplicación**: Implementa los casos de uso de la aplicación.
   - `application.seguridad`: Implementaciones de servicios (SeguridadServiceImpl)

3. **Capa de Infraestructura**: Proporciona implementaciones concretas de interfaces.
   - `infrastructure.configuration`: Configuraciones (SecurityConfig)
   - `infrastructure.seguridad.entity`: Entidades JPA
   - `infrastructure.seguridad.repository`: Implementaciones de repositorios
   - `infrastructure.seguridad.service`: Servicios de infraestructura (JwtTokenServiceImpl)

4. **Capa de Presentación**: Maneja la interacción con el usuario.
   - `presentation.controller`: Controladores REST (AuthController)
   - `presentation.dto`: Objetos de transferencia de datos (LoginRequestDto, LoginResponseDto)

## 4. Clases y Métodos

### 4.1. Capa de Configuración

#### SecurityConfig

Esta clase configura Spring Security para la aplicación.

**Métodos principales:**
- `authenticationManager()`: Configura el gestor de autenticación.
- `passwordEncoder()`: Define el codificador de contraseñas (BCrypt).
- `securityFilterChain()`: Configura la cadena de filtros de seguridad.
- `corsConfigurationSource()`: Configura CORS para permitir solicitudes desde orígenes específicos.

### 4.2. Capa de Dominio

#### TokenService (Interfaz)

Define operaciones para manejar tokens JWT.

**Métodos:**
- `generarTokenAcceso(UsuarioModel usuario)`: Genera un token de acceso para un usuario.
- `generarTokenRefresco(UsuarioModel usuario)`: Genera un token de refresco para un usuario.
- `extraerUsuario(String token)`: Extrae el nombre de usuario de un token.
- `esTokenValido(String token)`: Verifica si un token es válido.

#### SeguridadService (Interfaz)

Define operaciones de autenticación.

**Métodos:**
- `autenticacion(String username, String password)`: Autentica un usuario con credenciales.
- `refrescar(String token)`: Refresca los tokens usando un token de refresco.

#### Modelos de Dominio

- **UsuarioModel**: Representa un usuario en el sistema.
- **RolModel**: Representa un rol de usuario.
- **SeguridadModel**: Contiene tokens de autenticación.

### 4.3. Capa de Aplicación

#### SeguridadServiceImpl

Implementa la lógica de autenticación y refresco de tokens.

**Métodos principales:**
- `autenticacion(String username, String password)`: Autentica un usuario y genera tokens.
- `refrescar(String token)`: Valida un token de refresco y genera nuevos tokens.
- `generarTokensDesdeUsername(String username)`: Método auxiliar para generar tokens.

### 4.4. Capa de Infraestructura

#### JwtAuthenticationFilter

Filtra las solicitudes HTTP para procesar tokens JWT.

**Método principal:**
- `doFilterInternal()`: Extrae el token JWT del encabezado, lo valida y establece la autenticación.

#### CustomUserDetailsService

Carga usuarios desde el repositorio para la autenticación.

**Método principal:**
- `loadUserByUsername(String username)`: Busca un usuario por nombre de usuario.

#### JwtTokenServiceImpl

Implementa operaciones con tokens JWT.

**Métodos principales:**
- `generarTokenAcceso()`: Genera un token JWT de acceso con claims personalizados.
- `generarTokenRefresco()`: Genera un token JWT de refresco.
- `extraerUsuario()`: Extrae el nombre de usuario de un token.
- `esTokenValido()`: Verifica la validez de un token.

### 4.5. Capa de Presentación

#### AuthController

Proporciona endpoints REST para autenticación.

**Endpoints:**
- `POST /public/api/auth/login`: Autentica un usuario y devuelve tokens.
- `POST /public/api/auth/refresh`: Refresca los tokens usando un token de refresco.

#### DTOs (Data Transfer Objects)

- **LoginRequestDto**: Contiene credenciales de usuario (username, password).
- **LoginResponseDto**: Contiene tokens de autenticación y su duración.
- **RefreshTokenRequestDto**: Contiene un token de refresco.

## 5. Flujo de Autenticación

1. **Inicio de sesión:**
   - El cliente envía username y password al endpoint `/public/api/auth/login`.
   - `AuthController` llama a `SeguridadService.autenticacion()`.
   - `SeguridadServiceImpl` usa `AuthenticationManager` para validar credenciales.
   - Si son válidas, genera tokens de acceso y refresco usando `TokenService`.
   - Devuelve los tokens al cliente.

2. **Acceso a recursos protegidos:**
   - El cliente incluye el token de acceso en el encabezado Authorization.
   - `JwtAuthenticationFilter` intercepta la solicitud.
   - Extrae y valida el token usando `TokenService`.
   - Si es válido, carga los detalles del usuario con `CustomUserDetailsService`.
   - Establece la autenticación en el `SecurityContext`.
   - La solicitud continúa al controlador correspondiente.

3. **Refresco de tokens:**
   - Cuando el token de acceso expira, el cliente envía el token de refresco al endpoint `/public/api/auth/refresh`.
   - `AuthController` llama a `SeguridadService.refrescar()`.
   - `SeguridadServiceImpl` valida el token de refresco.
   - Si es válido, genera nuevos tokens de acceso y refresco.
   - Devuelve los nuevos tokens al cliente.

## 6. Ejemplos Prácticos

### Ejemplo 1: Iniciar sesión

**Solicitud:**
```http
POST /public/api/auth/login
Content-Type: application/json

{
  "username": "usuario1",
  "password": "contraseña123"
}
```

**Respuesta exitosa:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```

### Ejemplo 2: Acceder a un recurso protegido

**Solicitud:**
```http
GET /api/recurso-protegido
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Ejemplo 3: Refrescar tokens

**Solicitud:**
```http
POST /public/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Respuesta exitosa:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```