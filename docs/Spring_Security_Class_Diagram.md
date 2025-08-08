# Diagrama de Clases - Spring Security

## Estructura General

```
+------------------+     +------------------+     +------------------+
|  Presentation    |     |   Application    |     |     Domain       |
|  Layer           |     |   Layer          |     |     Layer        |
+------------------+     +------------------+     +------------------+
| - AuthController |     | - SeguridadSer-  |     | - SeguridadSer-  |
| - DTOs           |     |   viceImpl       |     |   vice (I)       |
+--------+---------+     +--------+---------+     | - TokenService(I)|
         |                        |                 | - Models         |
         |                        |                 +--------+---------+
         |                        |                          |
         |                        |                          |
         v                        v                          v
+------------------------------------------------------------------+
|                      Infrastructure Layer                         |
+------------------------------------------------------------------+
| - SecurityConfig                                                  |
| - JwtAuthenticationFilter                                         |
| - CustomUserDetails                                               |
| - CustomUserDetailsService                                        |
| - JwtTokenServiceImpl                                             |
| - Entities                                                        |
| - Repositories                                                    |
+------------------------------------------------------------------+
```

## Diagrama Detallado de Clases

```
+------------------------+       +------------------------+
| <<interface>>          |       | SeguridadServiceImpl  |
| SeguridadService       |<------| - authenticationMgr   |
| + autenticacion()      |       | - tokenService        |
| + refrescar()          |       | - userDetailsService  |
+------------------------+       | + autenticacion()     |
                                 | + refrescar()         |
                                 +------------------------+
                                          ^
                                          |
+------------------------+               |
| AuthController         |---------------+
| - seguridadService     |
| + login()              |
| + refresh()            |
+------------------------+
        ^
        |
+------------------------+       +------------------------+
| LoginRequestDto        |       | LoginResponseDto      |
| - username             |       | - token               |
| - password             |       | - refreshToken        |
+------------------------+       | - expiresIn           |
                                 +------------------------+

+------------------------+       +------------------------+
| <<interface>>          |       | JwtTokenServiceImpl   |
| TokenService           |<------| - claveSecreta        |
| + generarTokenAcceso() |       | - expiracionToken...  |
| + generarTokenRefresco()|      | + generarTokenAcceso()|
| + extraerUsuario()     |       | + generarTokenRefresco()|
| + esTokenValido()      |       | + extraerUsuario()    |
+------------------------+       | + esTokenValido()     |
                                 +------------------------+
                                          ^
                                          |
+------------------------+               |
| JwtAuthenticationFilter|---------------+
| - tokenService         |
| - userDetailsService   |
| + doFilterInternal()   |
+------------------------+
        ^
        |
+------------------------+       +------------------------+
| SecurityConfig         |-------| CustomUserDetailsService|
| - jwtAuthFilter        |       | - seguridadRepository |
| + securityFilterChain()|       | + loadUserByUsername()|
| + authenticationMgr()  |       +------------------------+
| + passwordEncoder()    |                ^
+------------------------+                |
                                          |
+------------------------+               |
| CustomUserDetails      |---------------+
| - usuario              |
| + getAuthorities()     |
| + getPassword()        |
| + getUsername()        |
| + isEnabled()          |
+------------------------+
        ^
        |
+------------------------+       +------------------------+
| UsuarioModel           |       | RolModel              |
| - id                   |-------| - id                  |
| - username             |       | - nombre              |
| - password             |       +------------------------+
| - nombre               |
| - apellido             |
| - activo               |
| - roles                |
+------------------------+
```

## Flujo de Autenticación

```
+----------+    1. Login Request     +---------------+
| Cliente  |------------------------>| AuthController|
|          |                         +---------------+
|          |                                |
|          |                                | 2. autenticacion()
|          |                                v
|          |                         +---------------+
|          |                         | SeguridadSer- |
|          |                         | viceImpl      |
|          |                         +---------------+
|          |                                |
|          |                                | 3. authenticate()
|          |                                v
|          |                         +---------------+
|          |                         | Authentication|
|          |                         | Manager       |
|          |                         +---------------+
|          |                                |
|          |                                | 4. loadUserByUsername()
|          |                                v
|          |                         +---------------+
|          |                         | CustomUserDet-|
|          |                         | ailsService   |
|          |                         +---------------+
|          |                                |
|          |                                | 5. generarTokens()
|          |                                v
|          |                         +---------------+
|          |                         | JwtTokenServi-|
|          |                         | ceImpl        |
|          |                         +---------------+
|          |                                |
|          |    6. Tokens Response          |
|          |<-------------------------------|
+----------+
```

## Flujo de Validación de Token

```
+----------+    1. Request with Token   +---------------+
| Cliente  |------------------------>| JwtAuthentica-|
|          |                         | tionFilter    |
|          |                         +---------------+
|          |                                |
|          |                                | 2. esTokenValido()
|          |                                v
|          |                         +---------------+
|          |                         | JwtTokenServi-|
|          |                         | ceImpl        |
|          |                         +---------------+
|          |                                |
|          |                                | 3. loadUserByUsername()
|          |                                v
|          |                         +---------------+
|          |                         | CustomUserDet-|
|          |                         | ailsService   |
|          |                         +---------------+
|          |                                |
|          |                                | 4. Set Authentication
|          |                                v
|          |                         +---------------+
|          |                         | SecurityConte-|
|          |                         | xtHolder      |
|          |                         +---------------+
|          |                                |
|          |    5. Continue to Controller   |
|          |<-------------------------------|
+----------+
```