package pe.cibertec.desarrollo_aplicaciones_web_1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "alumno_curso")
@Getter
@Setter
@ToString
public class AlumnoCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alumno_curso_id")
    private Long alumnoCursoId;

    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @Column(name = "curso")
    private String curso;
}