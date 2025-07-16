package pe.cibertec.desarrollo_aplicaciones_web_1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Alumno;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.AlumnoCurso;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.AlumnoCursoRepository;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.AlumnoRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaAvanzadoService {

    private final AlumnoRepository alumnoRepository;
    private final AlumnoCursoRepository alumnoCursoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // =================================================================
    // 1. FLUSHING - Sincronización manual entre memoria y base de datos
    // =================================================================

    /**
     * FLUSHING: Sincroniza los cambios del contexto de persistencia con la base de datos
     */
    public void demostrarFlushing() {
        log.info("🔄 === DEMOSTRACIÓN DE FLUSHING ===");

        // 1. Crear y guardar entidad (solo en memoria)
        Alumno alumno = new Alumno();
        alumno.setNombre("Franklin");
        alumno.setEmail("franklin@ejemplo.com");
        alumno = alumnoRepository.save(alumno);
        log.info("✅ Alumno guardado en MEMORIA: {} (ID: {})", alumno.getNombre(), alumno.getAlumnoId());

        // 2. En este punto, el INSERT aún no se ha ejecutado en la BD
        log.info("📝 El INSERT aún NO se ha ejecutado en la base de datos");

        // 3. Forzar sincronización con la base de datos
        entityManager.flush();
        log.info("📤 FLUSH ejecutado - INSERT ahora está en la base de datos");

        // 4. Verificar que está en la BD (sin usar caché)
        entityManager.clear(); // Limpiar caché para forzar consulta a BD
        Alumno resultado = entityManager.find(Alumno.class, alumno.getAlumnoId());
        log.info("📥 Alumno verificado desde BD: {}", resultado.getNombre());

        log.info("💡 Resumen: flush() sincroniza memoria → BD, pero NO hace commit");
    }

    // =================================================================
    // 2. BATCHING - Agrupación de operaciones para optimizar rendimiento
    // =================================================================

    /**
     * BATCHING: Agrupa múltiples operaciones SQL en lotes para mejorar rendimiento
     */
    public void demostrarBatching() {
        log.info("📦 === DEMOSTRACIÓN DE BATCHING ===");

        // 1. Preparar datos para inserción masiva
        List<Alumno> alumnos = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            Alumno alumno = new Alumno();
            alumno.setNombre("Alumno Batch " + i);
            alumno.setEmail("alumno" + i + "@batch.com");
            alumnos.add(alumno);
        }
        log.info("✅ Preparados {} alumnos para inserción en lote", alumnos.size());

        // 2. Medir tiempo de inserción
        long startTime = System.currentTimeMillis();

        // 3. Inserción usando batching
        alumnoRepository.saveAll(alumnos);

        long endTime = System.currentTimeMillis();
        log.info("📤 Inserción en lote completada en {} ms", (endTime - startTime));

        // 4. Verificar resultados
        List<Alumno> alumnosGuardados = alumnoRepository.findAll();
        log.info("📥 Total de alumnos en BD: {}", alumnosGuardados.size());

        log.info("💡 Resumen: Batching agrupa operaciones similares para mejor rendimiento");
    }

    // =================================================================
    // 3. FETCHING - Estrategias de carga de relaciones
    // =================================================================

    /**
     * FETCHING: Controla cómo y cuándo se cargan las relaciones entre entidades
     */
    public void demostrarFetching() {
        log.info("🔗 === DEMOSTRACIÓN DE FETCHING ===");

        // 1. Crear alumno
        Alumno alumno = new Alumno();
        alumno.setNombre("Juan Perez");
        alumno.setEmail("juan@ejemplo.com");
        alumno = alumnoRepository.save(alumno);
        log.info("✅ Alumno creado: {}", alumno.getNombre());

        // 2. Crear múltiples cursos para el alumno
        List<String> nombresCursos = Arrays.asList("Matemáticas", "Física", "Química", "Historia");
        for (String nombreCurso : nombresCursos) {
            AlumnoCurso curso = new AlumnoCurso();
            curso.setAlumno(alumno);
            curso.setCurso(nombreCurso);
            alumnoCursoRepository.save(curso);
        }
        log.info("📚 Creados {} cursos para el alumno", nombresCursos.size());

        // 3. Demostrar LAZY Loading (carga diferida)
        log.info("--- LAZY LOADING ---");
        AlumnoCurso curso = alumnoCursoRepository.findById(1L).orElse(null);
        log.info("📥 Curso cargado (sin alumno aún): {}", curso.getCurso());

        // Acceso a la relación dispara consulta adicional
        log.info("👤 Accediendo al alumno del curso...");
        String nombreAlumno = curso.getAlumno().getNombre(); // Aquí se ejecuta consulta adicional
        log.info("📥 Alumno cargado: {}", nombreAlumno);

        // 4. Demostrar JOIN FETCH (carga optimizada)
        log.info("--- JOIN FETCH ---");
        List<AlumnoCurso> cursosConAlumnos = alumnoCursoRepository.findAllWithAlumnos();
        log.info("📥 Cargados {} cursos con alumnos en una sola consulta", cursosConAlumnos.size());

        for (AlumnoCurso c : cursosConAlumnos) {
            log.info("📚 Curso: {} - Alumno: {}", c.getCurso(), c.getAlumno().getNombre());
        }

        log.info("💡 Resumen: LAZY evita cargas innecesarias, JOIN FETCH optimiza consultas");
    }

    // =================================================================
    // 4. CACHING - Caché de primer nivel (Session Cache)
    // =================================================================

    /**
     * CACHING: Sistema de caché para evitar consultas repetitivas
     *
     * Niveles de caché:
     * - Primer nivel (Session Cache): Automático, a nivel de EntityManager
     * - Segundo nivel (SessionFactory Cache): Opcional, entre sesiones
     *
     * Beneficios:
     * - Mejora rendimiento al evitar consultas repetidas
     * - Garantiza identidad de objetos dentro de la transacción
     * - Reduce carga en la base de datos
     */
    public void demostrarCaching() {
        log.info("💾 === DEMOSTRACIÓN DE CACHING ===");

        // 1. Crear y guardar alumno
        Alumno alumno = new Alumno();
        alumno.setNombre("Maria Gonzalez");
        alumno.setEmail("maria@ejemplo.com");
        alumno = alumnoRepository.save(alumno);
        Long alumnoId = alumno.getAlumnoId();
        log.info("✅ Alumno guardado: {} (ID: {})", alumno.getNombre(), alumnoId);

        // 2. Primera consulta - va a la base de datos
        log.info("--- PRIMERA CONSULTA ---");
        Alumno consulta1 = entityManager.find(Alumno.class, alumnoId);
        log.info("📥 Primera consulta ejecutada - Alumno: {}", consulta1.getNombre());

        // 3. Segunda consulta - usa caché (no va a BD)
        log.info("--- SEGUNDA CONSULTA ---");
        Alumno consulta2 = entityManager.find(Alumno.class, alumnoId);
        log.info("📥 Segunda consulta - Alumno: {}", consulta2.getNombre());

        // 4. Verificar identidad de objetos
        boolean mismaInstancia = (consulta1 == consulta2);
        boolean mismosValores = consulta1.equals(consulta2);
        log.info("🔍 ¿Misma instancia en memoria? {}", mismaInstancia);
        log.info("🔍 ¿Mismos valores? {}", mismosValores);

        // 5. Limpiar caché y consultar nuevamente
        log.info("--- DESPUÉS DE LIMPIAR CACHÉ ---");
        entityManager.clear(); // Limpiar caché de primer nivel
        Alumno consulta3 = entityManager.find(Alumno.class, alumnoId);
        log.info("📥 Tercera consulta (después de clear) - Alumno: {}", consulta3.getNombre());

        boolean nuevaInstancia = (consulta1 != consulta3);
        log.info("🔍 ¿Nueva instancia después de clear? {}", nuevaInstancia);

        log.info("💡 Resumen: Caché de primer nivel evita consultas repetidas en la misma transacción");
    }

    // =================================================================
    // 5. CONTROL DE CONCURRENCIA - Optimistic Locking
    // =================================================================

    /**
     * CONTROL DE CONCURRENCIA: Previene conflictos cuando múltiples usuarios
     * modifican la misma entidad simultáneamente
     *
     * Tipos de control:
     * - Optimista (@Version): Permite lecturas concurrentes, detecta conflictos al escribir
     * - Pesimista (Locks): Bloquea registro durante la lectura
     *
     * ¿Cómo funciona @Version?
     * - Agrega campo version a la entidad
     * - Se incrementa automáticamente en cada actualización
     * - Lanza OptimisticLockException si las versiones no coinciden
     */
    public void demostrarControlDeConcurrencia() {
        log.info("🔐 === DEMOSTRACIÓN DE CONTROL DE CONCURRENCIA ===");

        // 1. Crear alumno inicial
        Alumno alumno = new Alumno();
        alumno.setNombre("Carlos Ruiz");
        alumno.setEmail("carlos@ejemplo.com");
        alumno = alumnoRepository.save(alumno);
        entityManager.flush(); // Asegurar que está en BD

        Long alumnoId = alumno.getAlumnoId();
        Integer versionInicial = alumno.getVersion();
        log.info("✅ Alumno creado: {} (ID: {}, Version: {})",
                alumno.getNombre(), alumnoId, versionInicial);

        // 2. Simular dos usuarios obteniendo la misma entidad
        log.info("--- SIMULANDO ACCESO CONCURRENTE ---");
        entityManager.clear(); // Limpiar caché

        Alumno usuario1 = entityManager.find(Alumno.class, alumnoId);
        Alumno usuario2 = entityManager.find(Alumno.class, alumnoId);

        log.info("👤 Usuario 1 obtuvo alumno: {} (Version: {})",
                usuario1.getNombre(), usuario1.getVersion());
        log.info("👤 Usuario 2 obtuvo alumno: {} (Version: {})",
                usuario2.getNombre(), usuario2.getVersion());

        // 3. Usuario 1 modifica y guarda primero
        log.info("--- USUARIO 1 MODIFICA PRIMERO ---");
        usuario1.setNombre("Carlos Ruiz - Modificado por Usuario 1");
        usuario1.setEmail("carlos.user1@ejemplo.com");
        entityManager.merge(usuario1);
        entityManager.flush();

        // Recargar para ver nueva versión
        entityManager.refresh(usuario1);
        log.info("✅ Usuario 1 guardó cambios exitosamente (Nueva version: {})",
                usuario1.getVersion());

        // 4. Usuario 2 intenta modificar (debería fallar)
        log.info("--- USUARIO 2 INTENTA MODIFICAR ---");
        usuario2.setNombre("Carlos Ruiz - Modificado por Usuario 2");
        usuario2.setEmail("carlos.user2@ejemplo.com");

        try {
            entityManager.merge(usuario2);
            entityManager.flush();
            log.warn("❌ ERROR: No se detectó conflicto de concurrencia");
        } catch (OptimisticLockException e) {
            log.info("✅ Conflicto de concurrencia detectado correctamente");
            log.info("📝 Versión esperada: {}, Versión actual: {}",
                    usuario2.getVersion(), usuario1.getVersion());
        } catch (Exception e) {
            log.info("✅ Conflicto detectado: {}", e.getClass().getSimpleName());
        }

        // 5. Mostrar estado final
        entityManager.clear();
        Alumno estadoFinal = entityManager.find(Alumno.class, alumnoId);
        log.info("📊 Estado final: {} (Version: {})",
                estadoFinal.getNombre(), estadoFinal.getVersion());

        log.info("💡 Resumen: @Version previene modificaciones concurrentes conflictivas");
    }

    // =================================================================
    // 6. MÉTODO COMBINADO - Demostración completa
    // =================================================================

    public void demostracionCompleta() {
        log.info("🎯 === DEMOSTRACIÓN COMPLETA DE CONCEPTOS JPA ===");

        log.info("Ejecutando demostraciones individuales...");

        demostrarFlushing();
        log.info("");

        demostrarBatching();
        log.info("");

        demostrarFetching();
        log.info("");

        demostrarCaching();
        log.info("");

        demostrarControlDeConcurrencia();

        log.info("🎉 === TODAS LAS DEMOSTRACIONES COMPLETADAS ===");
    }

}
