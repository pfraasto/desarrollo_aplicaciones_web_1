package pe.cibertec.desarrollo_aplicaciones_web_1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "alumno")
@Getter
@Setter
@ToString
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alumno_id")
    private Long alumnoId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Version
    private int version; // Control de concurrencia optimista
}
