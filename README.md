
# 📘 Spring Data JPA con Spring Boot

> **Aplicación educativa** basada en Spring Boot y Spring Data JPA para aprender consultas personalizadas, parámetros, paginación y ordenamiento.

---

## 🎯 Objetivos de aprendizaje

Al finalizar este tema, dominarás los siguientes conceptos de Spring Data JPA:

| Concepto | Descripción | Beneficio |
|----------|-------------|-----------|
| **JPQL** | Consultas en lenguaje orientado a objetos | Consultas flexibles sin SQL nativo |
| **Parámetros** | Consultas con filtros dinámicos | Seguridad y reutilización |
| **Paginación** | División de resultados en páginas | Escalabilidad y eficiencia |
| **Ordenamiento** | Resultados ordenados dinámicamente | Mejora de UX y rendimiento |

---

## 🏗️ Arquitectura del proyecto

```
src/main/java/
    ├── controller/
    │   └── JpaPruebaComponent.java
    ├── entity/
    │   ├── Alumno.java
    │   └── AlumnoCurso.java
    ├── repository/
    │   ├── AlumnoRepository.java
    │   └── AlumnoCursoRepository.java
    └── service/
    └── JpaAvanzadoService.java
```

---

## 🧠 2.2.1 Introducción

Spring Data JPA permite crear consultas automáticamente a partir de nombres de métodos, pero también admite el uso de JPQL y SQL nativo.

**Ventajas:**
- Eliminación de código repetitivo
- Consultas orientadas a objetos (JPQL)
- Integración con Spring Boot y Hibernate

---

## 🧾 2.2.2 Consultas JPQL

### 🎯 Objetivo: Realizar consultas usando el lenguaje JPQL (Java Persistence Query Language).

**Ejemplo:** Buscar alumnos por nombre

```java
@Query("SELECT a FROM Alumno a WHERE a.nombre LIKE %:nombre%")
List<Alumno> buscarPorNombre(@Param("nombre") String nombre);
```

---

## 🔢 2.2.3 Uso de parámetros

### 🎯 Objetivo: Construir consultas dinámicas con parámetros seguros

**Ejemplo con múltiples parámetros:**

```java
@Query("SELECT a FROM Alumno a WHERE a.nombre = :nombre AND a.email = :email")
List<Alumno> buscarPorNombreYCorreo(@Param("nombre") String nombre, @Param("email") String email);
```

---

## 📄 2.2.4 Paginación y ordenamiento

### 🎯 Objetivo: Obtener grandes volúmenes de datos de forma eficiente

```java
Page<Alumno> findByNombreContaining(String nombre, Pageable pageable);
```

**Uso en servicio:**

```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());
Page<Alumno> resultados = alumnoRepository.findByNombreContaining("Juan", pageable);
```

---

## 📊 Ejemplo práctico

### Repositorio: \`AlumnoRepository.java\`

```java
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    @Query("SELECT a FROM Alumno a WHERE a.nombre LIKE %:nombre%")
    List<Alumno> buscarPorNombre(@Param("nombre") String nombre);

    Page<Alumno> findByNombreContaining(String nombre, Pageable pageable);
}
```

### Servicio: \`JpaAvanzadoService.java\`

```java
@Service
public class JpaAvanzadoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<Alumno> buscarPorNombre(String nombre) {
        return alumnoRepository.buscarPorNombre(nombre);
    }

    public Page<Alumno> listarPaginado(String filtro, int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("nombre").ascending());
        return alumnoRepository.findByNombreContaining(filtro, pageable);
    }
}
```

---

## 🔁 Flujo sugerido

```
Frontend Angular
        ↓
JpaPruebaComponent
        ↓
JpaAvanzadoService
        ↓
AlumnoRepository
        ↓
Base de Datos
```

---

## 🧪 Experimentos sugeridos

- [ ] Cambiar los filtros por email o ID
- [ ] Aplicar ordenamiento descendente por \`id\`
- [ ] Usar paginación en una tabla HTML
- [ ] Crear consultas con parámetros opcionales (usando \`@Query\` con \`CASE\` o \`IS NULL\`)

---

## 📚 Recursos para profundizar

| Recurso | Descripción | Nivel |
|---------|-------------|-------|
| [Spring Data JPA Docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/) | Referencia oficial | Básico |
| [JPQL Language Guide](https://docs.oracle.com/javaee/6/tutorial/doc/bnbpz.html) | Lenguaje de consultas | Intermedio |
| [Curso gratuito de Spring Data JPA](https://www.baeldung.com/spring-data-jpa-query) | Tutorial completo | Intermedio |
