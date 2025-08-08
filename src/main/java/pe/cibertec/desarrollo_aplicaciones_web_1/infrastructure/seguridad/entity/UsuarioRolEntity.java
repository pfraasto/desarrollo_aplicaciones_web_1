package pe.cibertec.desarrollo_aplicaciones_web_1.infrastructure.seguridad.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;

    private Boolean activo;
}
