
# 🚀 Spring Web MVC con Spring Boot - Creación de APIs REST

> Esta guía está centrada en la creación de **APIs RESTful** utilizando Spring Boot y el módulo **Spring Web MVC**.

---

## 🎯 Objetivos de aprendizaje

| Tema | Descripción |
|------|-------------|
| **Arquitectura** | Entender la estructura de una app REST con Spring |
| **Ciclo de vida** | Comprender cómo fluye una solicitud HTTP en Spring MVC |
| **Configuración** | Saber qué necesitas para que Spring Web funcione |
| **Estereotipos** | Usar anotaciones como `@RestController` y `@Service` correctamente |

---

## 🧱 2.3.1. Arquitectura para APIs REST

Spring Web MVC en Spring Boot sigue una arquitectura clara para exponer endpoints HTTP como servicios REST.

### 🧩 Estructura básica

```
src/main/java/
├── controller/        # Expone endpoints REST
├── service/           # Lógica de negocio
├── repository/        # Acceso a datos (JPA o simulados)
└── model/ (entity/)   # Clases de dominio
```

### 🧪 Ejemplo real

- `GET /api/alumnos` → Obtener lista de alumnos
- `POST /api/alumnos` → Registrar un alumno
- `PUT /api/alumnos/{id}` → Actualizar un alumno
- `DELETE /api/alumnos/{id}` → Eliminar un alumno

---

## 🔁 2.3.2. Ciclo de vida de una solicitud API

1. **Cliente realiza una petición HTTP** (`GET`, `POST`, `PUT`, `DELETE`)
2. **Spring Boot enruta la solicitud al controlador adecuado (`@RestController`)**
3. **El controlador llama al servicio (`@Service`)**
4. **El servicio accede al repositorio (`@Repository`)**
5. **La respuesta (JSON) se devuelve automáticamente al cliente**

---

## ⚙️ 2.3.3. Configuración mínima

### 🧱 Dependencia en `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 🛠️ No necesitas configurar vistas

Spring Boot ya incluye:

- Servidor embebido (Tomcat)
- Configuración automática de JSON con Jackson
- Serialización de objetos como JSON

---

## 🏷️ 2.3.4. Uso de estereotipos Spring para APIs

### ✅ @RestController

Expone una clase como API REST (retorna JSON directamente)

```java
@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    public List<Alumno> listar() {
        return alumnoService.listar();
    }

    @PostMapping
    public Alumno registrar(@RequestBody Alumno alumno) {
        return alumnoService.guardar(alumno);
    }

    @PutMapping("/{id}")
    public Alumno actualizar(@PathVariable Long id, @RequestBody Alumno alumno) {
        return alumnoService.actualizar(id, alumno);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        alumnoService.eliminar(id);
    }
}
```

### 🛠️ @Service

Contiene la lógica de negocio

```java
@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<Alumno> listar() {
        return alumnoRepository.findAll();
    }

    public Alumno guardar(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    public Alumno actualizar(Long id, Alumno datos) {
        Alumno alumno = alumnoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No encontrado"));
        alumno.setNombre(datos.getNombre());
        return alumnoRepository.save(alumno);
    }

    public void eliminar(Long id) {
        alumnoRepository.deleteById(id);
    }
}
```

### 🗃️ @Repository

Acceso a datos (en memoria o base de datos)

```java
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
}
```

---

## 🧪 Modelo de ejemplo

```java
@Entity
public class Alumno {
    @Id @GeneratedValue
    private Long id;
    private String nombre;
    private String email;

    // Getters y setters
}
```

---

## 🌐 Probar APIs

### 📦 Con Postman o curl

```bash
# GET lista de alumnos
curl http://localhost:8080/api/alumnos

# POST registrar alumno
curl -X POST http://localhost:8080/api/alumnos -H "Content-Type: application/json" -d '{"nombre":"Luis","email":"luis@ejemplo.com"}'
```

---

## ✅ Buenas prácticas

- Usar DTOs para no exponer entidades directamente
- Manejar errores con `@ExceptionHandler`
- Validar datos con `@Valid` y `@NotBlank`
- Documentar tus APIs con Swagger/OpenAPI

---

## 📚 Recursos útiles

| Recurso | Descripción |
|---------|-------------|
| [Spring Boot REST Docs](https://spring.io/guides/gs/rest-service/) | Guía oficial para REST |
| [Postman](https://www.postman.com/) | Cliente para probar APIs |
| [Baeldung Spring Boot REST](https://www.baeldung.com/spring-boot-rest-api) | Tutorial práctico |
| [Springdoc OpenAPI](https://springdoc.org/) | Documentación automática Swagger para Spring Boot |

---

## 🧩 Actividades sugeridas

- [ ] Crear un CRUD completo para `Alumno`
- [ ] Añadir validaciones con `javax.validation`
- [ ] Simular errores y manejar con `@ControllerAdvice`
- [ ] Probar APIs desde Angular o Flutter

