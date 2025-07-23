# 🧠 Tema 3: Fundamentos de Spring Boot

Aquí aprenderás los fundamentos de **Spring Boot**, una herramienta muy popular para crear aplicaciones web y de backend en Java de forma rápida y sencilla. Vamos a explorar los siguientes puntos:

---

## 📌 2.1.1. Introducción a Spring Boot

### ¿Qué es Spring Boot?

Spring Boot es un **framework** que nos permite crear aplicaciones Java más rápido. Nos ayuda a evitar configuraciones complejas y nos da muchas cosas listas para usar, como un servidor web incorporado, manejo de dependencias, conexión a base de datos, etc.

> 📦 Piensa en Spring Boot como un "kit de cocina" para hacer aplicaciones: ya viene con los ingredientes básicos y recetas para que no empieces desde cero.

---

## ⚙️ 2.1.2. Spring Initializr

### ¿Qué es Spring Initializr?

Es una página web que nos ayuda a **crear el esqueleto de una aplicación Spring Boot** en pocos clics.

👉 Sitio web: [https://start.spring.io](https://start.spring.io)

### ¿Cómo se usa?

1. Abre el sitio web.
2. Elige:
    - **Project**: Maven o Gradle (empezamos con Maven).
    - **Language**: Java.
    - **Spring Boot**: Usar la versión estable recomendada.
    - **Project Metadata**:
        - Group: `com.ejemplo`
        - Artifact: `demo`
3. Agrega dependencias:
    - Spring Web
    - Spring Boot DevTools
    - Lombok
4. Haz clic en "GENERATE" y se descargará un archivo `.zip` con tu proyecto listo.

> 📁 Luego, descomprime el archivo y ábrelo en tu IDE (como IntelliJ IDEA o VS Code con extensión Java).

---

## 🚀 2.1.3. Spring Boot Starters

### ¿Qué son los Starters?

Son **paquetes de dependencias preconfiguradas**. Nos ayudan a agregar funcionalidades comunes de forma fácil y rápida.

Ejemplos:
- `spring-boot-starter-web`: Para crear aplicaciones web y APIs REST.
- `spring-boot-starter-data-jpa`: Para trabajar con bases de datos.
- `spring-boot-starter-security`: Para agregar seguridad.

> ✅ Solo agregas un starter y automáticamente se configuran muchas cosas por ti.

### Ejemplo en `pom.xml` (Maven):

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## ♻️ 2.1.4. Uso de DevTools

### ¿Qué es DevTools?

**Spring Boot DevTools** mejora tu experiencia de desarrollo. Incluye:

- Recarga automática de la app cuando haces cambios en el código.
- Reinicio rápido del servidor.
- Mejores mensajes de error en navegador.

> 🔄 Ideal cuando estás haciendo cambios pequeños y no quieres reiniciar todo manualmente.

### ¿Cómo se agrega?

Si usaste Spring Initializr, solo asegúrate que esté en tu `pom.xml`:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-devtools</artifactId>
  <scope>runtime</scope>
</dependency>
```

> ⚠️ DevTools es solo para desarrollo, **no se debe usar en producción**.

---

## 🧬 2.1.5. Lombok

### ¿Qué es Lombok?

**Lombok** es una herramienta que **reduce el código repetitivo** en Java. Con unas cuantas anotaciones, puedes evitar escribir manualmente cosas como getters, setters, constructores, etc.

### Ejemplo:

```java
import lombok.Data;

@Data
public class Persona {
    private String nombre;
    private int edad;
}
```

Esto genera automáticamente:

- Getters y setters
- Constructor vacío
- `toString()`, `equals()`, y `hashCode`

> 📝 ¡Mucho menos código que escribir tú mismo!

### ¿Cómo usarlo?

1. Agrega la dependencia en tu `pom.xml`:

```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>


<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
      <annotationProcessorPaths>
        <path>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
        </path>
      </annotationProcessorPaths>
    </configuration>
  </plugin>
  <plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
      <excludes>
        <exclude>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
        </exclude>
      </excludes>
    </configuration>
  </plugin>
</plugins>
```

2. **Instala el plugin** de Lombok en tu IDE (IntelliJ o VS Code) para que reconozca el código generado.

---

## ✅ Resumen Rápido

| Concepto              | ¿Qué hace?                                                             |
|-----------------------|------------------------------------------------------------------------|
| Spring Boot           | Framework para crear apps Java rápido y fácil.                        |
| Spring Initializr     | Herramienta online para generar tu proyecto base.                     |
| Starters              | Dependencias listas para funcionalidades comunes.                     |
| DevTools              | Recarga automática y herramientas útiles para desarrollo.             |
| Lombok                | Elimina código repetitivo con anotaciones.                            |
